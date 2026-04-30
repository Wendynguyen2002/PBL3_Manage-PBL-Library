package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.common.PasswordChangeDTO;
import com.example.pblManagement.model.dto.user.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LecturerService {
    LecturerResponseDTO createLecturer(LecturerRequestDTO dto);

    LecturerResponseDTO getLecturerById(String id);

    LecturerResponseDTO updateLecturer(String id, LecturerRequestDTO dto);

    void deleteLecturer(String id);

    LecturerResponseDTO updateOwnProfile(LecturerSelfUpdateRequestDTO dto);

    void changePassword(PasswordChangeDTO dto);

    LecturerResponseDTO getOwnProfile();

    Page<LecturerSummaryDTO> getAllLecturers(String search, Pageable pageable);
}
