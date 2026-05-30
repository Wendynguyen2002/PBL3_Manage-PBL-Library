package com.example.pblManagement.service.impl;
import com.example.pblManagement.exceptions.InvalidCurrentPasswordException;
import com.example.pblManagement.mappers.AdminMapper;
import com.example.pblManagement.model.dto.common.PasswordChangeDTO;
import com.example.pblManagement.model.dto.user.*;
import com.example.pblManagement.model.entities.Admin;
import com.example.pblManagement.model.entities.enums.UserRole;
import com.example.pblManagement.repositories.AdminRepository;
import com.example.pblManagement.service.AdminService;
import com.example.pblManagement.utils.SecurityUtils;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final SecurityUtils securityUtils;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // Ensure exactly ONE admin exists at startup
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setId("ADMIN001");
            admin.setEmail("admin@example.com");
            admin.setFullName("System Administrator");
            admin.setRole(UserRole.ADMIN);
            admin.setPassword(passwordEncoder.encode("Admin@123456"));
            admin.setGender(null); // Optional fields can be null
            admin.setDateOfBirth(null);
            admin.setPhoneNumber(null);
            admin.setHomeTown(null);
            adminRepository.save(admin);
            System.out.println("Default admin created with email: admin@example.com");
        } else if (adminRepository.count() > 1) {
            // Safety check
            throw new IllegalStateException("Multiple admins found! System requires exactly one admin.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AdminResponseDTO getOwnProfile() {
        String currentUserId = securityUtils.getCurrentUserId();
        Admin admin = adminRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));
        return adminMapper.toResponseDTO(admin);
    }

    @Override
    public void changePassword(PasswordChangeDTO dto) {
        String currentAdminId = securityUtils.getCurrentUserId();
        Admin admin = adminRepository.findById(currentAdminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), admin.getPassword())) {
            throw new InvalidCurrentPasswordException("Current password is incorrect");
        }

        admin.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        adminRepository.save(admin);
    }
}
