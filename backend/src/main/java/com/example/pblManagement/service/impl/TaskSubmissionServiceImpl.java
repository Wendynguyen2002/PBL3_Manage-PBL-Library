package com.example.pblManagement.service.impl;

import com.example.pblManagement.mappers.SubmissionLinkMapper;
import com.example.pblManagement.mappers.TaskSubmissionMapper;
import com.example.pblManagement.model.dto.progress_task.SubmissionLinkRequestDTO;
import com.example.pblManagement.model.dto.progress_task.TaskSubmissionRequestDTO;
import com.example.pblManagement.model.dto.progress_task.TaskSubmissionResponseDTO;
import com.example.pblManagement.model.dto.progress_task.TaskSubmissionSummaryDTO;
import com.example.pblManagement.model.entities.*;
import com.example.pblManagement.model.entities.enums.TaskSubmissionStatus;
import com.example.pblManagement.model.entities.enums.UserRole;
import com.example.pblManagement.repositories.*;
import com.example.pblManagement.service.TaskSubmissionService;
import com.example.pblManagement.utils.PblClassAccessValidator;
import com.example.pblManagement.utils.TaskInClassValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskSubmissionServiceImpl implements TaskSubmissionService {
    private final TaskSubmissionRepository taskSubmissionRepository;
    private final TaskSubmissionMapper taskSubmissionMapper;
    private final PblGroupRepository pblGroupRepository;
    private final StudentRepository studentRepository;
    private final SubmissionLinkRepository submissionLinkRepository;
    private final SubmissionLinkMapper submissionLinkMapper;
    private final PblClassAccessValidator pblClassAccessValidator;
    private final TaskInClassValidator taskInClassValidator;

    // Student: Submit or resubmit
    @Transactional
    @Override
    public TaskSubmissionResponseDTO submitOrUpdateSubmission(Long taskId, String pblClassId,
                                                              TaskSubmissionRequestDTO dto, Account account) {
        // Only students can submit
        if (account.getRole() != UserRole.STUDENT) {
            throw new AccessDeniedException("Only students can submit tasks");
        }

        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        // Get student's group (must be in a group)
        PblGroup group = pblGroupRepository.findStudentGroupInClass(account.getId(), pblClassId)
                .orElseThrow(() -> new AccessDeniedException("You must be in a group to submit tasks"));

        // Validate task exists and belongs to class
        ProgressTask task = taskInClassValidator.validateTaskAndReturnEntity(taskId, pblClassId);

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

            // Initialize links list
            submission.setLinks(new ArrayList<>());

            // Save submission first to get ID for links
            submission = taskSubmissionRepository.save(submission);
        } else {
            // Update existing submission (resubmit)
            LocalDateTime now = LocalDateTime.now();

            submission.setBriefDescription(dto.getBriefDescription());
            submission.setSubmittedAt(now);  // UPDATE the submission time
            submission.setIsLate(now.isAfter(task.getDueDate()));  // recalculate based on new time
            submission.setLastModifiedBy(student);
            submission.setLastModifiedAt(now);

            // Delete old links and replace with new ones
            if (submission.getLinks() != null && !submission.getLinks().isEmpty()) {
                submissionLinkRepository.deleteAll(submission.getLinks());
                submission.getLinks().clear();
            }
        }

        // Handle links - create new links
        if (dto.getLinks() != null && !dto.getLinks().isEmpty()) {
            for (SubmissionLinkRequestDTO linkDto : dto.getLinks()) {
                if (linkDto.getUrl() != null && !linkDto.getUrl().trim().isEmpty()) {
                    SubmissionLink link = submissionLinkMapper.toEntity(linkDto);
                    link.setSubmission(submission);
                    submission.getLinks().add(link);
                }
            }
        }

        TaskSubmission savedSubmission = taskSubmissionRepository.save(submission);

        return taskSubmissionMapper.toResponseDTO(savedSubmission);
    }

    // Students: Get their group's submission
    @Transactional(readOnly = true)
    @Override
    public TaskSubmissionResponseDTO getMyGroupSubmission(Long taskId, String pblClassId, Account account) {
        // Only students can view their own submission
        if (account.getRole() != UserRole.STUDENT) {
            throw new AccessDeniedException("Only students can view their own submissions");
        }

        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        // Get student's group
        PblGroup group = pblGroupRepository.findStudentGroupInClass(account.getId(), pblClassId)
                .orElseThrow(() -> new AccessDeniedException("You must be in a group to submit tasks"));

        // Validate task
        taskInClassValidator.validateTask(taskId, pblClassId);

        // Find and return submission
        return taskSubmissionRepository
                .findByTaskIdAndGroupId(taskId, group.getId())
                .map(taskSubmissionMapper::toResponseDTO)
                .orElse(null);
    }

    // Lecturer: See all groups' submission for a task
    @Transactional(readOnly = true)
    @Override
    public List<TaskSubmissionSummaryDTO> getAllSubmissionsForTask(Long taskId, String pblClassId, Account account) {
        // Only lecturers can view all submissions
        if  (account.getRole() != UserRole.LECTURER) {
            throw new AccessDeniedException("Only lecturers can view all submissions");
        }

        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        // Validate task belongs to class
        taskInClassValidator.validateTask(taskId, pblClassId);

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

    // Lecturer: Get a specific group's submission details
    @Transactional(readOnly = true)
    @Override
    public TaskSubmissionResponseDTO getSubmissionByGroup(Long taskId, String pblClassId, Long groupId, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new AccessDeniedException("Only lecturers can view group submissions");
        }

        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        taskInClassValidator.validateTask(taskId, pblClassId);

        // Verify group exists in this class
        pblGroupRepository.findByIdAndPblClassId(groupId, pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found in this class"));

        // Find submission
        return taskSubmissionRepository
                .findByTaskIdAndGroupId(taskId, groupId)
                .map(taskSubmissionMapper::toResponseDTO)
                .orElse(null);
    }
}
