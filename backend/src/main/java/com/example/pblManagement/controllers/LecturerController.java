package com.example.pblManagement.controllers;

import com.example.pblManagement.model.dto.common.PasswordChangeDTO;
import com.example.pblManagement.model.dto.user.*;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.security.CurrentUser;
import com.example.pblManagement.service.LecturerService;
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
@RequestMapping("/api/lecturers")
@RequiredArgsConstructor
public class LecturerController {
    private final LecturerService lecturerService;

    // Admin: Create new lecturer
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LecturerResponseDTO> createLecturer(@Valid @RequestBody LecturerRequestDTO dto) {
        LecturerResponseDTO created = lecturerService.createLecturer(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Admin: Get lecturer by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LecturerResponseDTO> getLecturerById(@PathVariable String id) {
        return ResponseEntity.ok(lecturerService.getLecturerById(id));
    }

    // Admin: Full update lecturer
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LecturerResponseDTO> updateLecturer(
            @PathVariable String id,
            @Valid @RequestBody LecturerRequestDTO dto) {
        return ResponseEntity.ok(lecturerService.updateLecturer(id, dto));
    }

    // Admin: Delete lecturer
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLecturer(@PathVariable String id) {
        lecturerService.deleteLecturer(id);
        return ResponseEntity.noContent().build();
    }

    // Lecturer: Self-update (profile update)
    @PutMapping("/profile")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<LecturerResponseDTO> updateOwnProfile(
            @Valid @RequestBody LecturerSelfUpdateRequestDTO dto,
            @CurrentUser Account account) {
        return ResponseEntity.ok(lecturerService.updateOwnProfile(dto, account));
    }

    // Lecturer: Self change password
    @PutMapping("/profile/change-password")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody PasswordChangeDTO passwordChangeDTO,
            @CurrentUser Account account) {
        lecturerService.changePassword(passwordChangeDTO, account);
        return ResponseEntity.ok().build();
    }

    // Lecturer: View own profile
    @GetMapping("/profile")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<LecturerResponseDTO> getOwnProfile(@CurrentUser Account account) {
        return ResponseEntity.ok(lecturerService.getOwnProfile(account));
    }

    // Admin: Search lecturers
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<LecturerSummaryDTO>> getAllLecturers(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Pageable pageable = PageRequest.of(page, size,
                sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());

        return ResponseEntity.ok(lecturerService.getAllLecturers(search, pageable));
    }

    // Admin: Reset lecturer password to the original format
    @PostMapping("{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> resetLecturerPassword(@PathVariable String id) {
        lecturerService.resetLecturerPassword(id);

        // Return success message instead of the new password due to security concern
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successfully to the original format: Udn@{lecturerId}");
        response.put("lecturerId", id);

        return ResponseEntity.ok(response);
    }
}
