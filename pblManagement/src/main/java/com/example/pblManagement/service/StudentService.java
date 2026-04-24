package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.common.PasswordChangeDTO;
import com.example.pblManagement.model.dto.user.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {

    // Admin: Create new student
    StudentResponseDTO createStudent(StudentRequestDTO dto);

    // Admin: Get student by ID with full details
    StudentResponseDTO getStudentById(String id);

    // Admin: Get all students with search and pagination
    Page<StudentSummaryDTO> getAllStudents(String search, Pageable pageable);

    // Admin: Update student
    StudentResponseDTO updateStudent(String id, StudentRequestDTO dto);

    // Admin: Delete student
    void deleteStudent(String id);

    // student: Update own profile
    StudentResponseDTO updateOwnProfile(StudentSelfUpdateRequestDTO dto);

    // Separate endpoint for password change
    void changePassword(PasswordChangeDTO dto);

    // student: Get own profile
    StudentResponseDTO getOwnProfile();
}
