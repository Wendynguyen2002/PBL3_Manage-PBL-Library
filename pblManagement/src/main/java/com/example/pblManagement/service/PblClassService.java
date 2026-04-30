package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.pbl.PblClassRequestDTO;
import com.example.pblManagement.model.dto.pbl.PblClassResponseDTO;
import com.example.pblManagement.model.dto.pbl.PblClassSummaryDTO;
import com.example.pblManagement.model.dto.user.StudentResponseDTO;
import com.example.pblManagement.model.dto.user.StudentSummaryDTO;
import com.example.pblManagement.model.entities.Account;

import java.util.List;

public interface PblClassService {
    PblClassResponseDTO createPblClass(PblClassRequestDTO dto, Account account);

    List<PblClassSummaryDTO> getPblClassesForUser(Account account);

    PblClassResponseDTO getPblClassById(String PblClassId, Account account);

    // Get enrolled students for a class (tab 2)
    List<StudentSummaryDTO> getEnrolledStudents(String PblClassId, Account account);

    // Get specific student details from a class (on click in tab 2)
    StudentResponseDTO getStudentInClass(String pblClassId, String studentId, Account account);

    // Update PBL class
    PblClassResponseDTO updatePblClass(String pblClassId, PblClassRequestDTO dto, Account account);

    // Delete PBL class
    void deletePblClass(String PblClassId, Account account);
}
