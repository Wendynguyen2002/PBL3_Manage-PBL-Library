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
import com.example.pblManagement.utils.PblClassAccessValidator;
import com.example.pblManagement.utils.TaskInClassValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressTaskServiceImpl implements ProgressTaskService {
    private final ProgressTaskRepository progressTaskRepository;
    private final ProgressTaskMapper progressTaskMapper;
    private final PblClassRepository pblClassRepository;
    private final NotificationService notificationService;
    private final PblClassAccessValidator pblClassAccessValidator;
    private final TaskInClassValidator taskInClassValidator;

    // Lecturer: Create a new task
    @Transactional
    @Override
    public ProgressTaskResponseDTO createTask(String pblClassId, ProgressTaskRequestDTO dto, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new AccessDeniedException("Only lecturers can create a task");
        }

        PblClass pblClass = pblClassAccessValidator.findClassAndValidateAccessAndReturnEntity(pblClassId, account);

        ProgressTask task = progressTaskMapper.toEntity(dto);
        task.setPblClass(pblClass);

        ProgressTask savedTask = progressTaskRepository.save(task);

        // Send notifications to all enrolled students
        List<Student> students = pblClassRepository.findEnrolledStudentsByPblClassId(pblClassId);
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

    // Lecturer: Update a task
    @Transactional
    @Override
    public ProgressTaskResponseDTO updateTask(Long taskId, String pblClassId, ProgressTaskRequestDTO dto, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new AccessDeniedException("Only lecturers can update a task");
        }

        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        ProgressTask task = taskInClassValidator.validateTaskAndReturnEntity(taskId, pblClassId);

        // Store old title for notification
        String oldTitle = task.getTitle();

        progressTaskMapper.updateProgressTask(task, dto);

        ProgressTask savedTask = progressTaskRepository.save(task);

        // Send notifications to all enrolled students about the update
        List<Student> students = pblClassRepository.findEnrolledStudentsByPblClassId(pblClassId);
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

    // Lecturer: Delete a progress task
    @Transactional
    @Override
    public void deleteTask(Long taskId, String pblClassId, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new AccessDeniedException("Only lecturers can delete a task");
        }

        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        ProgressTask task = taskInClassValidator.validateTaskAndReturnEntity(taskId, pblClassId);

        progressTaskRepository.delete(task);
    }

    // All roles: See all tasks of a class
    @Transactional(readOnly = true)
    @Override
    public List<ProgressTaskSummaryDTO> getTasksByClass(String pblClassId, Account account) {
        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        List<ProgressTask> tasks = progressTaskRepository.findByPblClassIdOrderByDueDateAsc(pblClassId);
        return tasks.stream()
                .map(progressTaskMapper::toSummaryDTO)
                .toList();
    }

    // All roles: See details of a progress task
    @Transactional(readOnly = true)
    @Override
    public ProgressTaskResponseDTO getTaskById(Long taskId, String pblClassId, Account account) {
        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        ProgressTask task = taskInClassValidator.validateTaskAndReturnEntity(taskId, pblClassId);

        return progressTaskMapper.toResponseDTO(task);
    }

}
