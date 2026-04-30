package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.common.PasswordChangeDTO;
import com.example.pblManagement.model.dto.user.AdminRequestDTO;
import com.example.pblManagement.model.dto.user.AdminResponseDTO;
import com.example.pblManagement.model.dto.user.AdminSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    AdminResponseDTO createAdmin(AdminRequestDTO dto);

    AdminResponseDTO getAdminById(String id);

    Page<AdminSummaryDTO> getAllAdmins(String search, Pageable pageable);

    AdminResponseDTO updateAdmin(String id, AdminRequestDTO dto);

    void deleteAdmin(String id);

    void changePassword(PasswordChangeDTO dto);

    AdminResponseDTO getOwnProfile();
}
