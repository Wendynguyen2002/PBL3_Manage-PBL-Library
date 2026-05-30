package com.example.pblManagement.controllers;

import com.example.pblManagement.model.dto.pbl.PblClassRequestDTO;
import com.example.pblManagement.model.dto.pbl.PblClassResponseDTO;
import com.example.pblManagement.model.dto.pbl.PblClassSummaryDTO;
import com.example.pblManagement.model.dto.user.StudentResponseDTO;
import com.example.pblManagement.model.dto.user.StudentSummaryDTO;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.security.CurrentUser;
import com.example.pblManagement.service.PblClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pbl-classes")
@RequiredArgsConstructor
public class PblClassController {
    private final PblClassService pblClassService;

    // Lecturer: Create PBL class
    @PostMapping
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<PblClassResponseDTO> createPblClass(
            @Valid @RequestBody PblClassRequestDTO dto,
            @CurrentUser Account account) {
        PblClassResponseDTO created = pblClassService.createPblClass(dto, account);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // All roles: Show the list of PBL classes corresponding to the user's role
    @GetMapping
    public ResponseEntity<List<PblClassSummaryDTO>> getMyPblClasses(
            @CurrentUser Account account) {
        List<PblClassSummaryDTO> classes = pblClassService.getPblClassesForUser(account);
        return ResponseEntity.ok(classes);
    }

    // All roles: Get PBL class metadata (tab 1)
    @GetMapping("/{pblClassId}")
    public ResponseEntity<PblClassResponseDTO> getPblClassById(
            @PathVariable String pblClassId,
            @CurrentUser Account account) {
        PblClassResponseDTO pblClass = pblClassService.getPblClassById(pblClassId, account);
        return ResponseEntity.ok(pblClass);
    }

    // All roles: Get the list of enrolled students (tab 2)
    @GetMapping("/{pblClassId}/students")
    public ResponseEntity<List<StudentSummaryDTO>> getEnrolledStudents(
            @PathVariable String pblClassId,
            @CurrentUser Account account) {
        List<StudentSummaryDTO> students = pblClassService.getEnrolledStudents(pblClassId, account);
        return ResponseEntity.ok(students);
    }

    // Lecturer: Get available students that can be added (filtered by class majors)
    @GetMapping("/{pblClassId}/available-students")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<List<StudentSummaryDTO>> getAvailableStudents(
            @PathVariable String pblClassId,
            @CurrentUser Account account) {
        List<StudentSummaryDTO> availableStudents = pblClassService.getAvailableStudentsForClass(pblClassId, account);
        return ResponseEntity.ok(availableStudents);
    }

    // All roles: Get the details of a specific enrolled student from the list (tab 2 - modal popup)
    @GetMapping("/{pblClassId}/students/{studentId}")
    public ResponseEntity<StudentResponseDTO> getStudentInClass(
            @PathVariable String pblClassId,
            @PathVariable String studentId,
            @CurrentUser Account account) {
        StudentResponseDTO student = pblClassService.getStudentInClass(pblClassId, studentId, account);
        return ResponseEntity.ok(student);
    }

    // Lecturer: Update their own PBL class
    @PutMapping("/{pblClassId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<PblClassResponseDTO> updatePblClass(
            @PathVariable String pblClassId,
            @Valid @RequestBody PblClassRequestDTO dto,
            @CurrentUser Account account) {
        PblClassResponseDTO updated = pblClassService.updatePblClass(pblClassId, dto, account);
        return ResponseEntity.ok(updated);
    }

    // Lecturer: Delete their own PBL class / Admin: Delete any class
    @DeleteMapping("/{pblClassId}")
    @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')")
    public ResponseEntity<Void> deletePblClass(
            @PathVariable String pblClassId,
            @CurrentUser Account account) {
        pblClassService.deletePblClass(pblClassId, account);
        return ResponseEntity.noContent().build();
    }

    // Lecturer: Add students to their own classes
    @PostMapping("/{pblClassId}/students")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<Void> addStudentsToClass(
            @PathVariable String pblClassId,
            @RequestBody List<String> studentIds,
            @CurrentUser Account account) {
        pblClassService.addStudentsToClass(pblClassId, studentIds, account);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Lecturer: Remove student from their own classes
    @DeleteMapping("/{pblClassId}/students/{studentId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<Void> removeStudentFromClass(
            @PathVariable String pblClassId,
            @PathVariable String studentId,
            @CurrentUser Account account) {
        pblClassService.removeStudentFromClass(pblClassId, studentId, account);
        return ResponseEntity.noContent().build();
    }
}
