package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.others.MajorRequestDTO;
import com.example.pblManagement.model.dto.others.MajorSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


public interface MajorService {
    MajorSummaryDTO createMajor(MajorRequestDTO dto);

    MajorSummaryDTO getMajorById(String id);

    Page<MajorSummaryDTO> getAllMajors(String search, Pageable pageable);

    MajorSummaryDTO updateMajor(String id, MajorRequestDTO dto);

    void deleteMajor(String id);
}
