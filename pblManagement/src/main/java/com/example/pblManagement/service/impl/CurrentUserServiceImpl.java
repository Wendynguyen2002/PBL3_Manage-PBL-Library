package com.example.pblManagement.service.impl;

import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.repositories.AdminRepository;
import com.example.pblManagement.repositories.LecturerRepository;
import com.example.pblManagement.repositories.StudentRepository;
import com.example.pblManagement.service.CurrentUserService;
import com.example.pblManagement.utils.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {
    private final AdminRepository adminRepository;
    private final LecturerRepository lecturerRepository;
    private final StudentRepository studentRepository;

    @Override
    public Account getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            String userId = userDetails.getId();
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            assert role != null;
            return switch (role) {
                case "ROLE_ADMIN" -> adminRepository.findById(userId)
                        .orElseThrow(() -> new IllegalStateException("Admin not found"));
                case "ROLE_LECTURER" -> lecturerRepository.findById(userId)
                        .orElseThrow(() -> new IllegalStateException("Lecturer not found"));
                case "ROLE_STUDENT" -> studentRepository.findById(userId)
                        .orElseThrow(() -> new IllegalStateException("Student not found"));
                default -> throw new IllegalStateException("Unknown role: " + role);
            };
        }

        throw new IllegalStateException("Current user not found");
    }

    @Override
    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw new IllegalStateException("Current user ID not found");
    }
}
