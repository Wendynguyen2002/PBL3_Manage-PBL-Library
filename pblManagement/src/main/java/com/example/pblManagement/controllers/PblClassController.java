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

    // Create PBL class
    @PostMapping
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<PblClassResponseDTO> createPblClass(
            @Valid @RequestBody PblClassRequestDTO dto,
            @CurrentUser Account account) {
        PblClassResponseDTO created = pblClassService.createPblClass(dto, account);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Show the list of PBL classes corresponding to the role of users
    @GetMapping
    public ResponseEntity<List<PblClassSummaryDTO>> getMyPblClasses(
            @CurrentUser Account account) {
        List<PblClassSummaryDTO> classes = pblClassService.getPblClassesForUser(account);
        return ResponseEntity.ok(classes);
    }

    // Get PBL class metadata
    @GetMapping("/{pblClassId}")
    public ResponseEntity<PblClassResponseDTO> getPblClassById(
            @PathVariable String pblClassId,
            @CurrentUser Account account) {
        PblClassResponseDTO pblClass = pblClassService.getPblClassById(pblClassId, account);
        return ResponseEntity.ok(pblClass);
    }

    // Get the list of enrolled students
    @GetMapping("/{pblClassId}/students")
    public ResponseEntity<List<StudentSummaryDTO>> getEnrolledStudents(
            @PathVariable String pblClassId,
            @CurrentUser Account account) {
        List<StudentSummaryDTO> students = pblClassService.getEnrolledStudents(pblClassId, account);
        return ResponseEntity.ok(students);
    }

    // Get available students that can be added (filtered by class majors)
    @GetMapping("/{pblClassId}/available-students")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<List<StudentSummaryDTO>> getAvailableStudents(
            @PathVariable String pblClassId,
            @CurrentUser Account account) {
        List<StudentSummaryDTO> availableStudents = pblClassService.getAvailableStudentsForClass(pblClassId, account);
        return ResponseEntity.ok(availableStudents);
    }

    // Get the details of a specific enrolled student from the list
    @GetMapping("/{pblClassId}/students/{studentId}")
    public ResponseEntity<StudentResponseDTO> getStudentInClass(
            @PathVariable String pblClassId,
            @PathVariable String studentId,
            @CurrentUser Account account) {
        StudentResponseDTO student = pblClassService.getStudentInClass(pblClassId, studentId, account);
        return ResponseEntity.ok(student);
    }

//    @GetMapping("/{classId}/students/count")
//    public ResponseEntity<Long> getEnrolledStudentsCount(
//            @PathVariable String classId,
//            @CurrentUser Account currentUser) {
//        long count = pblClassService.getEnrolledStudentsCount(classId, currentUser);
//        return ResponseEntity.ok(count);
//    }

    // Update PBL class
    @PutMapping("/{pblClassId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<PblClassResponseDTO> updatePblClass(
            @PathVariable String pblClassId,
            @Valid @RequestBody PblClassRequestDTO dto,
            @CurrentUser Account account) {
        PblClassResponseDTO updated = pblClassService.updatePblClass(pblClassId, dto, account);
        return ResponseEntity.ok(updated);
    }

    // Delete PBL class
    @DeleteMapping("/{pblClassId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<Void> deletePblClass(
            @PathVariable String pblClassId,
            @CurrentUser Account account) {
        pblClassService.deletePblClass(pblClassId, account);
        return ResponseEntity.noContent().build();
    }

    // Add students to class
    @PostMapping("/{pblClassId}/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<Void> addStudentsToClass(
            @PathVariable String pblClassId,
            @RequestBody List<String> studentIds,
            @CurrentUser Account account) {
        pblClassService.addStudentsToClass(pblClassId, studentIds, account);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Remove student from class
    @DeleteMapping("/{pblClassId}/students/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LECTURER')")
    public ResponseEntity<Void> removeStudentFromClass(
            @PathVariable String pblClassId,
            @PathVariable String studentId,
            @CurrentUser Account account) {
        pblClassService.removeStudentFromClass(pblClassId, studentId, account);
        return ResponseEntity.noContent().build();
    }
}
