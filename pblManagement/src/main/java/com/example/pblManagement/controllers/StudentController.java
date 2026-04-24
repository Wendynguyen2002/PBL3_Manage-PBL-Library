package com.example.pblManagement.controllers;

import com.example.pblManagement.model.dto.common.PasswordChangeDTO;
import com.example.pblManagement.model.dto.user.*;
import com.example.pblManagement.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    // Admin only: Create new student
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentResponseDTO> createStudent(@Valid @RequestBody StudentRequestDTO dto) {
        StudentResponseDTO created = studentService.createStudent(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Admin only: Get student by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable String id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    // Admin only: Full update student
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentResponseDTO> updateStudent(@PathVariable String id, @Valid @RequestBody StudentRequestDTO dto) {
        return ResponseEntity.ok(studentService.updateStudent(id, dto));
    }

    // Admin only: Delete student
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // student self-update (profile update)
    @PutMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentResponseDTO> updateOwnProfile(@Valid @RequestBody StudentSelfUpdateRequestDTO dto) {
        return ResponseEntity.ok(studentService.updateOwnProfile(dto));
    }

    // student self change password
    @PutMapping("/profile/change-password")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody PasswordChangeDTO passwordChangeDTO) {
        studentService.changePassword(passwordChangeDTO);
        return ResponseEntity.ok().build();
    }

    // student view own profile
    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentResponseDTO> getOwnProfile() {
        return ResponseEntity.ok(studentService.getOwnProfile());
    }

    // Admin only: Search students
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<StudentSummaryDTO>> getAllStudents(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "department.departmentName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Pageable pageable = PageRequest.of(page, size,
                sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());

        return ResponseEntity.ok(studentService.getAllStudents(search, pageable));
    }
}
