package com.example.pblManagement.service.impl;

import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.model.entities.enums.UserRole;
import com.example.pblManagement.repositories.AdminRepository;
import com.example.pblManagement.repositories.LecturerRepository;
import com.example.pblManagement.repositories.StudentRepository;
import com.example.pblManagement.service.CurrentUserService;
import com.example.pblManagement.utils.SecurityUtils;
import com.example.pblManagement.utils.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {
    private final SecurityUtils securityUtils;
    private final AdminRepository adminRepository;
    private final LecturerRepository lecturerRepository;
    private final StudentRepository studentRepository;

    @Override
    public Account getCurrentAccount() {
        UserDetailsImpl userDetails = securityUtils.getCurrentUserDetails();
        String userId = userDetails.getId();
        UserRole role = userDetails.getRole();

        return switch (role) {
            case ADMIN -> adminRepository.findById(userId)
                    .orElseThrow(() -> new IllegalStateException("Admin not found"));
            case LECTURER -> lecturerRepository.findById(userId)
                    .orElseThrow(() -> new IllegalStateException("Lecturer not found"));
            case STUDENT -> studentRepository.findById(userId)
                    .orElseThrow(() -> new IllegalStateException("Student not found"));
        };
    }

    @Override
    public String getCurrentUserId() {
        return securityUtils.getCurrentUserId();
    }
}