package com.example.pblManagement.controllers;

import com.example.pblManagement.model.dto.pbl.PblGroupSummaryDTO;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.security.CurrentUser;
import com.example.pblManagement.service.PblGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pbl-classes/{pblClassId}/groups")
@RequiredArgsConstructor
public class PblGroupController {
    private final PblGroupService pblGroupService;

    // Get all groups in a class (anyone enrolled/assigned can view)
    @GetMapping
    public ResponseEntity<List<PblGroupSummaryDTO>> getGroupsByClass(
            @PathVariable String pblClassId,
            @CurrentUser Account account) {
        return ResponseEntity.ok(pblGroupService.getGroupsByClass(pblClassId, account));
    }

    // Student: Create group
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PblGroupSummaryDTO> createGroup(
            @PathVariable String pblClassId,
            @RequestParam(required = false) Long projectId,
            @CurrentUser Account account) {
        PblGroupSummaryDTO group = pblGroupService.createGroup(pblClassId, projectId, account);
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    // Student: Update their own group's project
    @PutMapping("/{groupId}/project")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> updateGroupProject(
            @PathVariable Long groupId,
            @RequestParam(required = false) Long projectId,
            @CurrentUser Account account) {
        pblGroupService.updateGroupProject(groupId, projectId, account);
        return ResponseEntity.ok().build();
    }

    // Student: Disband group (only if they're the only member)
    @DeleteMapping("/{groupId}/disband")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> disbandGroup(
            @PathVariable Long groupId,
            @CurrentUser Account account) {
        pblGroupService.disbandGroup(groupId, account);
        return ResponseEntity.noContent().build();
    }

    // Student: Join a group
    @PostMapping("/{groupId}/join")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> joinGroup(
            @PathVariable String pblClassId,
            @PathVariable Long groupId,
            @CurrentUser Account account) {
        pblGroupService.joinGroup(groupId, pblClassId, account);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Lecturer: Remove student from group
    @DeleteMapping("/{groupId}/students/{studentId}")
    @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')")
    public ResponseEntity<Void> removeStudentFromGroup(
            @PathVariable String pblClassId,
            @PathVariable Long groupId,
            @PathVariable String studentId,
            @CurrentUser Account account) {
        pblGroupService.removeStudentFromGroup(groupId, studentId, pblClassId, account);
        return ResponseEntity.noContent().build();
    }

    // Lecturer: Delete group
    @DeleteMapping("/{groupId}")
    @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable String pblClassId,
            @PathVariable Long groupId,
            @CurrentUser Account account) {
        pblGroupService.deleteGroup(groupId, pblClassId, account);
        return ResponseEntity.noContent().build();
    }
}
