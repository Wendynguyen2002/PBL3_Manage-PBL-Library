package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.common.PasswordChangeDTO;
import com.example.pblManagement.model.dto.user.AdminResponseDTO;

public interface AdminService {

    AdminResponseDTO getOwnProfile();

    void changePassword(PasswordChangeDTO dto);
}
