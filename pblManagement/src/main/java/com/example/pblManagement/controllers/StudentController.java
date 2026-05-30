package com.example.pblManagement.controllers;

import com.example.pblManagement.model.dto.common.PasswordChangeDTO;
import com.example.pblManagement.model.dto.user.*;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.security.CurrentUser;
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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    // Admin: Create new student
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentResponseDTO> createStudent(@Valid @RequestBody StudentRequestDTO dto) {
        StudentResponseDTO created = studentService.createStudent(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Admin: Get student by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable String id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    // Admin: Full update student
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable String id,
            @Valid @RequestBody StudentRequestDTO dto) {
        return ResponseEntity.ok(studentService.updateStudent(id, dto));
    }

    // Admin: Delete student
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // Student: Self-update (profile update)
    @PutMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentResponseDTO> updateOwnProfile(
            @Valid @RequestBody StudentSelfUpdateRequestDTO dto,
            @CurrentUser Account account) {
        return ResponseEntity.ok(studentService.updateOwnProfile(dto, account));
    }

    // Student: Self change password
    @PutMapping("/profile/change-password")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody PasswordChangeDTO passwordChangeDTO,
            @CurrentUser Account account) {
        studentService.changePassword(passwordChangeDTO, account);
        return ResponseEntity.ok().build();
    }

    // Student: View own profile
    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentResponseDTO> getOwnProfile(@CurrentUser Account account) {
        return ResponseEntity.ok(studentService.getOwnProfile(account));
    }

    // Admin: Search students
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<StudentSummaryDTO>> getAllStudents(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Pageable pageable = PageRequest.of(page, size,
                sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());

        return ResponseEntity.ok(studentService.getAllStudents(search, pageable));
    }

    // Admin: Reset student password to the original format
    @PostMapping("{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> resetStudentPassword(@PathVariable String id) {
        studentService.resetStudentPassword(id);

        // Return success message instead of the new password due to security concern
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successfully to the original format: Dut@{studentId}");
        response.put("studentId", id);

        return ResponseEntity.ok(response);
    }
}
