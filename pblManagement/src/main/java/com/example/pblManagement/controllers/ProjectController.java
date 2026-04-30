package com.example.pblManagement.controllers;

import com.example.pblManagement.model.dto.project.ProjectRequestDTO;
import com.example.pblManagement.model.dto.project.ProjectResponseDTO;
import com.example.pblManagement.model.dto.project.ProjectSummaryDTO;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.security.CurrentUser;
import com.example.pblManagement.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pbl-classes/{pblClassId}/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<ProjectResponseDTO> createProject (
            @PathVariable String pblClassId,
            @Valid @RequestBody ProjectRequestDTO dto,
            @CurrentUser Account account) {
        ProjectResponseDTO created = projectService.createProject(pblClassId, dto, account);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProjectSummaryDTO>> getProjectsByPblClass(
            @PathVariable String pblClassId,
            @CurrentUser Account account) {
        List<ProjectSummaryDTO> projects = projectService.getProjectsByPblClass(pblClassId, account);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(
            @PathVariable String pblClassId,
            @PathVariable Long projectId,
            @CurrentUser Account account) {
        ProjectResponseDTO project = projectService.getProjectById(projectId, pblClassId, account);
        return ResponseEntity.ok(project);
    }

    @PutMapping("/{projectId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<ProjectResponseDTO> updateProject(
            @PathVariable String pblClassId,
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectRequestDTO dto,
            @CurrentUser Account account) {
        ProjectResponseDTO updated = projectService.updateProject(projectId, pblClassId, dto, account);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{projectId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<Void> deleteProject(
            @PathVariable String pblClassId,
            @PathVariable Long projectId,
            @CurrentUser Account account) {
        projectService.deleteProject(projectId, pblClassId, account);
        return ResponseEntity.noContent().build();
    }
}
