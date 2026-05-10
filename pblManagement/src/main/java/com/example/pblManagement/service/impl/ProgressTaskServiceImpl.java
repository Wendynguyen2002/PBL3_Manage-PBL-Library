package com.example.pblManagement.service.impl;

import com.example.pblManagement.mappers.ProgressTaskMapper;
import com.example.pblManagement.model.dto.progress_task.ProgressTaskRequestDTO;
import com.example.pblManagement.model.dto.progress_task.ProgressTaskResponseDTO;
import com.example.pblManagement.model.dto.progress_task.ProgressTaskSummaryDTO;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.model.entities.PblClass;
import com.example.pblManagement.model.entities.ProgressTask;
import com.example.pblManagement.model.entities.Student;
import com.example.pblManagement.model.entities.enums.UserRole;
import com.example.pblManagement.repositories.PblClassRepository;
import com.example.pblManagement.repositories.ProgressTaskRepository;
import com.example.pblManagement.service.NotificationService;
import com.example.pblManagement.service.ProgressTaskService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgressTaskServiceImpl implements ProgressTaskService {
    private final ProgressTaskRepository progressTaskRepository;
    private final ProgressTaskMapper progressTaskMapper;
    private final PblClassRepository pblClassRepository;
    private final NotificationService notificationService;

    // Helper: Validate lecturer owns the class
    private PblClass validateLecturerAccess(String pblClassId, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new IllegalStateException("Only lecturers can manage tasks");
        }

        PblClass pblClass = pblClassRepository.findById(pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("PBL class not found: " + pblClassId));

        if (pblClass.getLecturer() == null || !pblClass.getLecturer().getId().equals(account.getId())) {
            throw new IllegalStateException("You can only manage tasks for your own classes");
        }

        return pblClass;
    }

    // Helper: Validate class access for viewing (students, lecturers, admins)
    private void validateViewAccess(String pblClassId, Account account) {
        PblClass pblClass = pblClassRepository.findById(pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("PBL class not found: " + pblClassId));

        switch (account.getRole()) {
            case ADMIN -> {
            }
            case LECTURER -> {
                if (pblClass.getLecturer() != null && pblClass.getLecturer().getId().equals(account.getId())) {
                    break;
                }
                throw new IllegalStateException("You don't have access to this class");
            }
            case STUDENT -> {
                if (pblClassRepository.isStudentEnrolledInClass(pblClassId, account.getId())) {
                    break;
                }
                throw new IllegalStateException("You are not enrolled in this class");
            }
        }
    }

    // Helper: Get all enrolled students in a class (for notifications)
    private List<Student> getEnrolledStudents(String pblClassId) {
        return pblClassRepository.findEnrolledStudentsByPblClassId(pblClassId);
    }

    @Override
    public ProgressTaskResponseDTO createTask(String pblClassId, ProgressTaskRequestDTO dto, Account account) {
        PblClass pblClass = validateLecturerAccess(pblClassId, account);

        ProgressTask task = progressTaskMapper.toEntity(dto);
        task.setPblClass(pblClass);

        ProgressTask savedTask = progressTaskRepository.save(task);

        // Send notifications to all enrolled students
        List<Student> students = getEnrolledStudents(pblClassId);
        for (Student student : students) {
            notificationService.createNotification(
                    student.getId(),
                    UserRole.STUDENT,
                    "New Progress Task: " + savedTask.getTitle(),
                    String.format("A new task '%s' has been created for class '%s'. Due date: %s",
                            savedTask.getTitle(),
                            pblClass.getClassName(),
                            savedTask.getDueDate()),
                    "TASK_CREATED",
                    String.valueOf(savedTask.getId())
            );
        }

        return progressTaskMapper.toResponseDTO(savedTask);
    }

    @Override
    public ProgressTaskResponseDTO updateTask(Long taskId, String pblClassId, ProgressTaskRequestDTO dto, Account account) {
        validateLecturerAccess(pblClassId, account);

        ProgressTask task = progressTaskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + taskId));

        if (!task.getPblClass().getId().equals(pblClassId)) {
            throw new ValidationException("Task does not belong to this class");
        }

        // Store old title for notification
        String oldTitle = task.getTitle();

        progressTaskMapper.updateProgressTask(task, dto);

        ProgressTask savedTask = progressTaskRepository.save(task);

        // Send notifications to all enrolled students about the update
        List<Student> students = getEnrolledStudents(pblClassId);
        for (Student student : students) {
            notificationService.createNotification(
                    student.getId(),
                    UserRole.STUDENT,
                    "Task Updated: " + savedTask.getTitle(),
                    String.format("Task '%s' (previously '%s') has been updated for class '%s'. New due date: %s",
                            savedTask.getTitle(),
                            oldTitle,
                            task.getPblClass().getClassName(),
                            savedTask.getDueDate()),
                    "TASK_UPDATED",
                    String.valueOf(savedTask.getId())
            );
        }

        return progressTaskMapper.toResponseDTO(savedTask);
    }

    @Override
    public void deleteTask(Long taskId, String pblClassId, Account account) {
        validateLecturerAccess(pblClassId, account);

        ProgressTask task = progressTaskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + taskId));

        if (!task.getPblClass().getId().equals(pblClassId)) {
            throw new ValidationException("Task does not belong to this class");
        }

        progressTaskRepository.delete(task);
    }

    @Override
    public List<ProgressTaskSummaryDTO> getTasksByClass(String pblClassId, Account account) {
        validateViewAccess(pblClassId, account);

        List<ProgressTask> tasks = progressTaskRepository.findByPblClassIdOrderByDueDateAsc(pblClassId);
        return tasks.stream()
                .map(progressTaskMapper::toSummaryDTO)
                .toList();
    }

    @Override
    public ProgressTaskResponseDTO getTaskById(Long taskId, String pblClassId, Account account) {
        validateViewAccess(pblClassId, account);

        ProgressTask task = progressTaskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + taskId));

        if (!task.getPblClass().getId().equals(pblClassId)) {
            throw new ValidationException("Task does not belong to this class");
        }

        return progressTaskMapper.toResponseDTO(task);
    }
}
