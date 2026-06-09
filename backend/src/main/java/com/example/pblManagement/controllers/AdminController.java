package com.example.pblManagement.controllers;

import com.example.pblManagement.model.dto.common.PasswordChangeDTO;
import com.example.pblManagement.model.dto.user.AdminResponseDTO;
import com.example.pblManagement.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    // Admin: View own profile
    @GetMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminResponseDTO> getProfile() {
        return ResponseEntity.ok(adminService.getOwnProfile());
    }

    // Admin: Change own password
    @PostMapping("/change-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody PasswordChangeDTO dto) {
        adminService.changePassword(dto);
        return ResponseEntity.ok().build();
    }
}
