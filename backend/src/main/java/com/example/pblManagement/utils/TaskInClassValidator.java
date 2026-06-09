package com.example.pblManagement.utils;

import com.example.pblManagement.model.entities.ProgressTask;
import com.example.pblManagement.repositories.ProgressTaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskInClassValidator {
    private final ProgressTaskRepository progressTaskRepository;

    public ProgressTask validateTaskAndReturnEntity(Long taskId, String pblClassId) {
        ProgressTask task = progressTaskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + taskId));

        if (!task.getPblClass().getId().equals(pblClassId)) {
            throw new IllegalStateException("Task does not belong to this class");
        }

        return task;
    }

    public void validateTask(Long taskId, String pblClassId) {
        ProgressTask task = progressTaskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + taskId));

        if (!task.getPblClass().getId().equals(pblClassId)) {
            throw new IllegalStateException("Task does not belong to this class");
        }
    }
}
