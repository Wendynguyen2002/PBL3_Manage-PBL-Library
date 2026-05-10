package com.example.pblManagement.service.impl;

import com.example.pblManagement.mappers.SubmissionLinkMapper;
import com.example.pblManagement.mappers.TaskSubmissionMapper;
import com.example.pblManagement.model.dto.progress_task.TaskSubmissionRequestDTO;
import com.example.pblManagement.model.dto.progress_task.TaskSubmissionResponseDTO;
import com.example.pblManagement.model.dto.progress_task.TaskSubmissionSummaryDTO;
import com.example.pblManagement.model.entities.*;
import com.example.pblManagement.model.entities.enums.TaskSubmissionStatus;
import com.example.pblManagement.model.entities.enums.UserRole;
import com.example.pblManagement.repositories.*;
import com.example.pblManagement.service.TaskSubmissionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskSubmissionServiceImpl implements TaskSubmissionService {
    private final TaskSubmissionRepository taskSubmissionRepository;
    private final ProgressTaskRepository progressTaskRepository;
    private final TaskSubmissionMapper taskSubmissionMapper;
    private final PblClassRepository pblClassRepository;
    private final PblGroupRepository pblGroupRepository;
    private final StudentRepository studentRepository;
    private final SubmissionLinkRepository submissionLinkRepository;
    private final SubmissionLinkMapper submissionLinkMapper;

    // Helper: Validate student is in a group for this class
    private PblGroup getStudentGroupInClass(String studentId, String pblClassId) {
        return pblGroupRepository.findStudentGroupInClass(studentId, pblClassId)
                .orElseThrow(() -> new ValidationException("You must be in a group to submit tasks"));
    }

    // Helper: Validate task belongs to class and return it
    private ProgressTask validateTaskInClass(Long taskId, String pblClassId) {
        ProgressTask task = progressTaskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + taskId));

        if (!task.getPblClass().getId().equals(pblClassId)) {
            throw new ValidationException("Task does not belong to this class");
        }

        return task;
    }

    // Helper: Validate lecturer access to class
    private void validateLecturerAccess(String pblClassId, Account account) {
        if (account.getRole() != UserRole.LECTURER && account.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("Only lecturers can view all submissions");
        }

        PblClass pblClass = pblClassRepository.findById(pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("PBL class not found: " + pblClassId));

        if (account.getRole() == UserRole.LECTURER) {
            if (pblClass.getLecturer() == null || !pblClass.getLecturer().getId().equals(account.getId())) {
                throw new IllegalStateException("You don't have access to this class");
            }
        }
    }

    @Override
    public TaskSubmissionResponseDTO submitOrUpdateSubmission(Long taskId, String pblClassId,
                                                              TaskSubmissionRequestDTO dto, Account account) {
        // Only students can submit
        if (account.getRole() != UserRole.STUDENT) {
            throw new IllegalStateException("Only students can submit tasks");
        }

        // Verify student is enrolled in class
        if (!pblClassRepository.isStudentEnrolledInClass(pblClassId, account.getId())) {
            throw new ValidationException("You are not enrolled in this class");
        }

        // Get student's group (must be in a group)
        PblGroup group = getStudentGroupInClass(account.getId(), pblClassId);

        // Validate task exists and belongs to class
        ProgressTask task = validateTaskInClass(taskId, pblClassId);

        // Get student entity
        Student student = studentRepository.findById(account.getId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        // Check if submission already exists
        TaskSubmission submission = taskSubmissionRepository
                .findByTaskIdAndGroupId(taskId, group.getId())
                .orElse(null);

        boolean isNewSubmission = (submission == null);

        if (isNewSubmission) {
            // Create new submission
            submission = taskSubmissionMapper.toEntity(dto);
            submission.setTask(task);
            submission.setGroup(group);
            submission.setSubmittedBy(student);
            submission.setStatus(TaskSubmissionStatus.SUBMITTED);

            // Calculate isLate
            LocalDateTime now = LocalDateTime.now();
            submission.setSubmittedAt(now);
            submission.setIsLate(now.isAfter(task.getDueDate()));

            // Save submission first to get ID for links
            submission = taskSubmissionRepository.save(submission);
        } else {
            // Update existing submission
            submission.setBriefDescription(dto.getBriefDescription());
            submission.setLastModifiedBy(student);
            submission.setLastModifiedAt(LocalDateTime.now());

            // Recalculate isLate (in case due date changed after submission)
            LocalDateTime now = LocalDateTime.now();
            if (submission.getSubmittedAt() == null) {
                submission.setSubmittedAt(now);
            }
            submission.setIsLate(now.isAfter(task.getDueDate()));

            // Delete old links and replace with new ones
            if (submission.getLinks() != null) {
                submissionLinkRepository.deleteAll(submission.getLinks());
                submission.getLinks().clear();
            }
        }

        // Handle links (same for both create and update)
        final TaskSubmission finalSubmission = submission;
        if (dto.getLinks() != null && !dto.getLinks().isEmpty()) {
            List<SubmissionLink> links = dto.getLinks().stream()
                    .map(linkDto -> {
                        SubmissionLink link = submissionLinkMapper.toEntity(linkDto);
                        link.setSubmission(finalSubmission);
                        return link;
                    })
                    .toList();
            finalSubmission.getLinks().addAll(links);
        }

        TaskSubmission savedSubmission = taskSubmissionRepository.save(submission);

        return taskSubmissionMapper.toResponseDTO(savedSubmission);
    }

    @Override
    public TaskSubmissionResponseDTO getMyGroupSubmission(Long taskId, String pblClassId, Account account) {
        // Only students can view their own submission
        if (account.getRole() != UserRole.STUDENT) {
            throw new IllegalStateException("Only students can view their own submissions");
        }

        // Verify student is enrolled
        if (!pblClassRepository.isStudentEnrolledInClass(pblClassId, account.getId())) {
            throw new ValidationException("You are not enrolled in this class");
        }

        // Get student's group
        PblGroup group = getStudentGroupInClass(account.getId(), pblClassId);

        // Validate task
        validateTaskInClass(taskId, pblClassId);

        // Find submission
        TaskSubmission submission = taskSubmissionRepository
                .findByTaskIdAndGroupId(taskId, group.getId())
                .orElseThrow(() -> new EntityNotFoundException("No submission found for your group"));

        return taskSubmissionMapper.toResponseDTO(submission);
    }

    @Override
    public List<TaskSubmissionSummaryDTO> getAllSubmissionsForTask(Long taskId, String pblClassId, Account account) {
        // Only lecturers can view all submissions
        validateLecturerAccess(pblClassId, account);

        // Validate task belongs to class
        validateTaskInClass(taskId, pblClassId);

        // Get all groups in this class
        List<PblGroup> groups = pblGroupRepository.findByPblClassIdOrderByGroupName(pblClassId);

        // For each group, find their submission (or null if not submitted)
        return groups.stream()
                .map(group -> {
                    TaskSubmission submission = taskSubmissionRepository
                            .findByTaskIdAndGroupId(taskId, group.getId())
                            .orElse(null);

                    if (submission == null) {
                        // Return "not submitted" summary
                        return TaskSubmissionSummaryDTO.builder()
                                .groupId(group.getId())
                                .groupName(group.getGroupName())
                                .hasSubmitted(false)
                                .submittedAt(null)
                                .isLate(null)
                                .status(TaskSubmissionStatus.NOT_SUBMITTED)
                                .submittedByStudentName(null)
                                .build();
                    }

                    return taskSubmissionMapper.toSummaryDTO(submission);
                })
                .collect(Collectors.toList());
    }

    @Override
    public TaskSubmissionResponseDTO getSubmissionByGroup(Long taskId, String pblClassId, Long groupId, Account account) {
        // Only lecturers can view any group's submission
        validateLecturerAccess(pblClassId, account);

        // Validate task belongs to class
        validateTaskInClass(taskId, pblClassId);

        // Verify group exists in this class
        pblGroupRepository.findByIdAndPblClassId(groupId, pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found in this class"));

        // Find submission
        TaskSubmission submission = taskSubmissionRepository
                .findByTaskIdAndGroupId(taskId, groupId)
                .orElseThrow(() -> new EntityNotFoundException("No submission found for this group"));

        return taskSubmissionMapper.toResponseDTO(submission);
    }
}
