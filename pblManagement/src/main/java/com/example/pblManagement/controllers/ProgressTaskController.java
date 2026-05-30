package com.example.pblManagement.controllers;

import com.example.pblManagement.model.dto.progress_task.ProgressTaskRequestDTO;
import com.example.pblManagement.model.dto.progress_task.ProgressTaskResponseDTO;
import com.example.pblManagement.model.dto.progress_task.ProgressTaskSummaryDTO;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.security.CurrentUser;
import com.example.pblManagement.service.ProgressTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pbl-classes/{pblClassId}/tasks")
@RequiredArgsConstructor
public class ProgressTaskController {
    private final ProgressTaskService progressTaskService;

    // All roles: Get all tasks in a class
    @GetMapping
    public ResponseEntity<List<ProgressTaskSummaryDTO>> getTasksByClass(
            @PathVariable String pblClassId,
            @CurrentUser Account account) {
        return ResponseEntity.ok(progressTaskService.getTasksByClass(pblClassId, account));
    }

    // All roles: Get single task details
    @GetMapping("/{taskId}")
    public ResponseEntity<ProgressTaskResponseDTO> getTaskById(
            @PathVariable String pblClassId,
            @PathVariable Long taskId,
            @CurrentUser Account account) {
        return ResponseEntity.ok(progressTaskService.getTaskById(taskId, pblClassId, account));
    }

    // Lecturer: Create task
    @PostMapping
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<ProgressTaskResponseDTO> createTask(
            @PathVariable String pblClassId,
            @Valid @RequestBody ProgressTaskRequestDTO dto,
            @CurrentUser Account account) {
        ProgressTaskResponseDTO created = progressTaskService.createTask(pblClassId, dto, account);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Lecturer: Update task
    @PutMapping("/{taskId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<ProgressTaskResponseDTO> updateTask(
            @PathVariable String pblClassId,
            @PathVariable Long taskId,
            @Valid @RequestBody ProgressTaskRequestDTO dto,
            @CurrentUser Account account) {
        ProgressTaskResponseDTO updated = progressTaskService.updateTask(taskId, pblClassId, dto, account);
        return ResponseEntity.ok(updated);
    }

    // Lecturer: Delete task
    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<Void> deleteTask(
            @PathVariable String pblClassId,
            @PathVariable Long taskId,
            @CurrentUser Account account) {
        progressTaskService.deleteTask(taskId, pblClassId, account);
        return ResponseEntity.noContent().build();
    }
}
