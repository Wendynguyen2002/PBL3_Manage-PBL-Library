package com.example.pblManagement.service.impl;

import com.example.pblManagement.mappers.MajorMapper;
import com.example.pblManagement.model.dto.others.MajorRequestDTO;
import com.example.pblManagement.model.dto.others.MajorSummaryDTO;
import com.example.pblManagement.model.entities.Major;
import com.example.pblManagement.repositories.DepartmentRepository;
import com.example.pblManagement.repositories.MajorRepository;
import com.example.pblManagement.service.MajorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MajorServiceImpl implements MajorService {
    private final MajorMapper majorMapper;
    private final MajorRepository majorRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public MajorSummaryDTO createMajor(MajorRequestDTO dto) {
        // Check if major already exists
        if (majorRepository.existsById(dto.getId())) {
            throw new IllegalArgumentException("Major with ID " + dto.getId() + " already exists");
        }

        // Verify department exists
        if (!departmentRepository.existsById(dto.getDepartmentId())) {
            throw new IllegalArgumentException("Department not found with ID: " + dto.getDepartmentId());
        }

        Major major = majorMapper.toEntity(dto);
        return majorMapper.toSummaryDTO(majorRepository.save(major));
    }

    @Override
    public MajorSummaryDTO getMajorById(String id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Major not found with ID: " + id));
        return majorMapper.toSummaryDTO(major);
    }

    @Override
    public Page<MajorSummaryDTO> getAllMajors(String search, Pageable pageable) {
        Page<Major> majorsPage;

        if (search == null || search.trim().isEmpty()) {
            majorsPage = majorRepository.findAll(pageable);
        } else {
            majorsPage = majorRepository.searchMajors(search.trim(), pageable);
        }

        return majorsPage.map(majorMapper::toSummaryDTO);
    }

    @Override
    public MajorSummaryDTO updateMajor(String id, MajorRequestDTO dto) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Major not found with ID: " + id));

        // Check ID uniqueness ONLY if the ID is changing
        if (!id.equals(dto.getId()) && majorRepository.existsById(dto.getId())) {
            throw new IllegalArgumentException("Major with ID " + dto.getId() + " already exists");
        }

        // Check department existence if changing
        if (!major.getDepartment().getId().equals(dto.getDepartmentId())) {
            if (!departmentRepository.existsById(dto.getDepartmentId())) {
                throw new IllegalArgumentException("Department not found with ID: " + dto.getDepartmentId());
            }
        }

        majorMapper.updateMajor(major, dto);

        return majorMapper.toSummaryDTO(majorRepository.save(major));
    }

    @Override
    public void deleteMajor(String id) {
        if (!majorRepository.existsById(id)) {
            throw new IllegalArgumentException("Major not found with ID: " + id);
        }
        majorRepository.deleteById(id);
    }
}
