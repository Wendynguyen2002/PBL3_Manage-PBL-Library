package com.example.pblManagement.controllers;

import com.example.pblManagement.model.dto.progress_task.TaskSubmissionRequestDTO;
import com.example.pblManagement.model.dto.progress_task.TaskSubmissionResponseDTO;
import com.example.pblManagement.model.dto.progress_task.TaskSubmissionSummaryDTO;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.security.CurrentUser;
import com.example.pblManagement.service.TaskSubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pbl-classes/{pblClassId}/tasks/{taskId}/submissions")
@RequiredArgsConstructor
public class TaskSubmissionController {
    private final TaskSubmissionService taskSubmissionService;

    // Student: Submit or update their group's submission
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<TaskSubmissionResponseDTO> submitOrUpdateSubmission(
            @PathVariable String pblClassId,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskSubmissionRequestDTO dto,
            @CurrentUser Account account) {
        TaskSubmissionResponseDTO submission = taskSubmissionService.submitOrUpdateSubmission(taskId, pblClassId, dto, account);
        return ResponseEntity.status(HttpStatus.CREATED).body(submission);
    }

    // Student: Get their group's submission
    @GetMapping("/my-group")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<TaskSubmissionResponseDTO> getMyGroupSubmission(
            @PathVariable String pblClassId,
            @PathVariable Long taskId,
            @CurrentUser Account account) {
        TaskSubmissionResponseDTO submission = taskSubmissionService.getMyGroupSubmission(taskId, pblClassId, account);
        return ResponseEntity.ok(submission);
    }

    // Lecturer: Get all submissions for a task (one per group)
    @GetMapping
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<List<TaskSubmissionSummaryDTO>> getAllSubmissionsForTask(
            @PathVariable String pblClassId,
            @PathVariable Long taskId,
            @CurrentUser Account account) {
        List<TaskSubmissionSummaryDTO> submissions = taskSubmissionService.getAllSubmissionsForTask(taskId, pblClassId, account);
        return ResponseEntity.ok(submissions);
    }

    // Lecturer: Get a specific group's submission details
    @GetMapping("/groups/{groupId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<TaskSubmissionResponseDTO> getSubmissionByGroup(
            @PathVariable String pblClassId,
            @PathVariable Long taskId,
            @PathVariable Long groupId,
            @CurrentUser Account account) {
        TaskSubmissionResponseDTO submission = taskSubmissionService.getSubmissionByGroup(taskId, pblClassId, groupId, account);
        return ResponseEntity.ok(submission);
    }
}
