package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.common.PasswordChangeDTO;
import com.example.pblManagement.model.dto.user.*;
import com.example.pblManagement.model.entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface StudentService {
    StudentResponseDTO createStudent(StudentRequestDTO dto);

    StudentResponseDTO getStudentById(String id);

    Page<StudentSummaryDTO> getAllStudents(String search, Pageable pageable);

    StudentResponseDTO updateStudent(String id, StudentRequestDTO dto);

    void deleteStudent(String id);

    void resetStudentPassword(String id);

    StudentResponseDTO updateOwnProfile(StudentSelfUpdateRequestDTO dto, Account account);

    void changePassword(PasswordChangeDTO dto, Account account);

    StudentResponseDTO getOwnProfile(Account account);
}
