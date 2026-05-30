package com.example.pblManagement.service.impl;

import com.example.pblManagement.exceptions.DuplicateResourceException;
import com.example.pblManagement.mappers.MajorMapper;
import com.example.pblManagement.model.dto.others.MajorRequestDTO;
import com.example.pblManagement.model.dto.others.MajorSummaryDTO;
import com.example.pblManagement.model.entities.Major;
import com.example.pblManagement.repositories.DepartmentRepository;
import com.example.pblManagement.repositories.MajorRepository;
import com.example.pblManagement.service.MajorService;
import com.example.pblManagement.utils.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MajorServiceImpl implements MajorService {
    private final MajorMapper majorMapper;
    private final MajorRepository majorRepository;
    private final DepartmentRepository departmentRepository;
    private final SecurityUtils securityUtils;

    // ADMIN: Create a new major, must assign a corresponding department
    @Override
    public MajorSummaryDTO createMajor(MajorRequestDTO dto) {
        securityUtils.verifyAdmin();

        // Check if major already exists
        if (majorRepository.existsById(dto.getId())) {
            throw new DuplicateResourceException("Major with ID " + dto.getId() + " already exists");
        }

        // Verify department exists
        if (!departmentRepository.existsById(dto.getDepartmentId())) {
            throw new EntityNotFoundException("Department not found with ID: " + dto.getDepartmentId());
        }

        Major major = majorMapper.toEntity(dto);
        return majorMapper.toSummaryDTO(majorRepository.save(major));
    }

    // Get details of a major by ID
    @Override
    @Transactional(readOnly = true)
    public MajorSummaryDTO getMajorById(String id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Major not found with ID: " + id));
        return majorMapper.toSummaryDTO(major);
    }

    // Get the list of all majors with search and pagination
    @Override
    @Transactional(readOnly = true)
    public Page<MajorSummaryDTO> getAllMajors(String search, Pageable pageable) {
        Page<Major> majorsPage;

        if (search == null || search.trim().isEmpty()) {
            majorsPage = majorRepository.findAll(pageable);
        } else {
            majorsPage = majorRepository.searchMajors(search.trim(), pageable);
        }

        return majorsPage.map(majorMapper::toSummaryDTO);
    }

    // Lecturer: Get all majors for a department with no pagination, for checklist on creating PBL classes
    @Override
    @Transactional(readOnly = true)
    public List<MajorSummaryDTO> getMajorsByDepartment(String departmentId) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new EntityNotFoundException("Department not found with ID: " + departmentId);
        }

        List<Major> majors = majorRepository.findByDepartmentId(departmentId);
        return majors.stream()
                .map(majorMapper::toSummaryDTO)
                .toList();
    }

    // Admin: Update a major
    @Override
    public MajorSummaryDTO updateMajor(String id, MajorRequestDTO dto) {
        securityUtils.verifyAdmin();

        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Major not found with ID: " + id));

        // Check ID uniqueness ONLY if the ID is changing
        if (!id.equals(dto.getId()) && majorRepository.existsById(dto.getId())) {
            throw new DuplicateResourceException("Major with ID " + dto.getId() + " already exists");
        }

        // Check department existence if changing
        if (!major.getDepartment().getId().equals(dto.getDepartmentId())) {
            if (!departmentRepository.existsById(dto.getDepartmentId())) {
                throw new EntityNotFoundException("Department not found with ID: " + dto.getDepartmentId());
            }
        }

        majorMapper.updateMajor(major, dto);

        return majorMapper.toSummaryDTO(majorRepository.save(major));
    }

    // Admin: Delete a major
    @Override
    public void deleteMajor(String id) {
        securityUtils.verifyAdmin();

        if (!majorRepository.existsById(id)) {
            throw new EntityNotFoundException("Major not found with ID: " + id);
        }
        majorRepository.deleteById(id);
    }
}
