package com.example.pblManagement.service.impl;

import com.example.pblManagement.mappers.DepartmentMapper;
import com.example.pblManagement.model.dto.others.DepartmentRequestDTO;
import com.example.pblManagement.model.dto.others.DepartmentResponseDTO;
import com.example.pblManagement.model.entities.Department;
import com.example.pblManagement.repositories.DepartmentRepository;
import com.example.pblManagement.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentMapper departmentMapper;
    private final DepartmentRepository departmentRepository;

    @Override
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO dto) {
        if (departmentRepository.existsById(dto.getId())) {
            throw new IllegalArgumentException("Department with ID " + dto.getId() + " already exists");
        }
        Department department = departmentMapper.toEntity(dto);
        return departmentMapper.toResponseDTO(departmentRepository.save(department));
    }

    @Override
    public DepartmentResponseDTO getDepartmentById(String id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + id));
        return departmentMapper.toResponseDTO(department);
    }

    @Override
    public DepartmentResponseDTO updateDepartment(String id, DepartmentRequestDTO dto) {
        if (departmentRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Department not found with ID: " + id);
        }

        Department department = departmentMapper.toEntity(dto);
        return departmentMapper.toResponseDTO(departmentRepository.save(department));
    }

    @Override
    public void deleteDepartment(String id) {
        if (departmentRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Department not found with ID: " + id);
        }
        departmentRepository.deleteById(id);
    }

    @Override
    public Page<DepartmentResponseDTO> getAllDepartments(String search, Pageable pageable) {
        Page<Department> departmentsPage;

        if (search == null || search.trim().isEmpty()) {
            departmentsPage = departmentRepository.findAll(pageable);
        } else {
            departmentsPage = departmentRepository.searchDepartments(search.trim(), pageable);
        }

        return departmentsPage.map(departmentMapper::toResponseDTO);
    }
}
