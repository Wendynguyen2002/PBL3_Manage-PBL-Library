package com.example.pblManagement.service.impl;
import com.example.pblManagement.mappers.AdminMapper;
import com.example.pblManagement.model.dto.common.PasswordChangeDTO;
import com.example.pblManagement.model.dto.user.*;
import com.example.pblManagement.model.entities.Admin;
import com.example.pblManagement.repositories.AdminRepository;
import com.example.pblManagement.service.AdminService;
import com.example.pblManagement.utils.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // Admin: Create new admin
    @Override
    public AdminResponseDTO createAdmin(AdminRequestDTO dto) {
        // Check if mail already exists
        if (adminRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Admin admin = adminMapper.toEntity(dto);

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            admin.setPassword(passwordEncoder.encode(admin.getId())); // Default password is the admin's ID
        }

        return adminMapper.toResponseDTO(adminRepository.save(admin));
    }

    // Admin: Get admin by ID with full details
    @Override
    public AdminResponseDTO getAdminById(String id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));
        return adminMapper.toResponseDTO(admin);
    }

    // Admin: Get all admins with search and pagination
    @Override
    public Page<AdminSummaryDTO> getAllAdmins(String search, Pageable pageable) {
        Page<Admin> adminsPage;

        if (search == null || search.trim().isEmpty()) {
            adminsPage = adminRepository.findAll(pageable);
        } else {
            adminsPage = adminRepository.searchAdmins(search.trim(), pageable);
        }

        return adminsPage.map(adminMapper::toSummaryDTO);
    }

    // Admin: Update admin
    @Override
    public AdminResponseDTO updateAdmin(String id, AdminRequestDTO dto) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));

        // Check email uniqueness if changed
        if (!admin.getEmail().equals(dto.getEmail()) && adminRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        adminMapper.updateAdmin(admin, dto);

        // Only update password if provided
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return adminMapper.toResponseDTO(adminRepository.save(admin));
    }

    // Admin: Delete admin
    @Override
    public void deleteAdmin(String id) {
        if (!adminRepository.existsById(id)) {
            throw new EntityNotFoundException("Admin not found with id: " + id);
        }
        adminRepository.deleteById(id);
    }

    // Separate endpoint for password change
    @Override
    public void changePassword(PasswordChangeDTO dto) {
        String currentAdminId = securityUtils.getCurrentUserId();
        Admin admin = adminRepository.findById(currentAdminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));

        // Verify current password
        if (!passwordEncoder.matches(dto.getCurrentPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Set new password
        admin.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        adminRepository.save(admin);
    }

    // admin: Get own profile
    @Override
    public AdminResponseDTO getOwnProfile() {
        String currentUserId = securityUtils.getCurrentUserId();
        Admin admin = adminRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));
        return adminMapper.toResponseDTO(admin);
    }
}
