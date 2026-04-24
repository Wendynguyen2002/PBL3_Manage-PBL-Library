package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.common.PasswordChangeDTO;
import com.example.pblManagement.model.dto.user.AdminRequestDTO;
import com.example.pblManagement.model.dto.user.AdminResponseDTO;
import com.example.pblManagement.model.dto.user.AdminSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    // Admin: Create new admin
    AdminResponseDTO createAdmin(AdminRequestDTO dto);

    // Admin: Get admin by ID with full details
    AdminResponseDTO getAdminById(String id);

    // Admin: Get all admins with search and pagination
    Page<AdminSummaryDTO> getAllAdmins(String search, Pageable pageable);

    // Admin: Update admin
    AdminResponseDTO updateAdmin(String id, AdminRequestDTO dto);

    // Admin: Delete admin
    void deleteAdmin(String id);

    // Separate endpoint for password change
    void changePassword(PasswordChangeDTO dto);

    // admin: Get own profile
    AdminResponseDTO getOwnProfile();
}
