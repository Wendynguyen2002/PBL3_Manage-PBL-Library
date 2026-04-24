package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.common.PasswordChangeDTO;
import com.example.pblManagement.model.dto.user.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LecturerService {

    // Admin: Create new lecturer
    LecturerResponseDTO createLecturer(LecturerRequestDTO dto);

    // Admin: Get lecturer by ID with full details
    LecturerResponseDTO getLecturerById(String id);

    // Admin: Update lecturer
    LecturerResponseDTO updateLecturer(String id, LecturerRequestDTO dto);

    // Admin: Delete lecturer
    void deleteLecturer(String id);

    // Lecturer: Update own profile
    LecturerResponseDTO updateOwnProfile(LecturerSelfUpdateRequestDTO dto);

    // Separate endpoint for password change
    void changePassword(PasswordChangeDTO dto);

    // Lecturer: Get own profile
    LecturerResponseDTO getOwnProfile();

    // Admin: Search for lecturers
    Page<LecturerSummaryDTO> getAllLecturers(String search, Pageable pageable);
}
