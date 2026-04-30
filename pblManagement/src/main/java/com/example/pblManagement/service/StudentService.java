package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.common.PasswordChangeDTO;
import com.example.pblManagement.model.dto.user.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {
    StudentResponseDTO createStudent(StudentRequestDTO dto);

    StudentResponseDTO getStudentById(String id);

    Page<StudentSummaryDTO> getAllStudents(String search, Pageable pageable);

    StudentResponseDTO updateStudent(String id, StudentRequestDTO dto);

    void deleteStudent(String id);

    StudentResponseDTO updateOwnProfile(StudentSelfUpdateRequestDTO dto);

    void changePassword(PasswordChangeDTO dto);

    StudentResponseDTO getOwnProfile();
}
