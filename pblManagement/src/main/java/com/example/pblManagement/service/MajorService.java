package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.others.MajorRequestDTO;
import com.example.pblManagement.model.dto.others.MajorSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface MajorService {
    MajorSummaryDTO createMajor(MajorRequestDTO dto);

    MajorSummaryDTO getMajorById(String id);

    Page<MajorSummaryDTO> getAllMajors(String search, Pageable pageable);

    List<MajorSummaryDTO> getMajorsByDepartment(String departmentId);

    MajorSummaryDTO updateMajor(String id, MajorRequestDTO dto);

    void deleteMajor(String id);
}
