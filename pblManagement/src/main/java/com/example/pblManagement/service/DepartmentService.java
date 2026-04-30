package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.others.DepartmentRequestDTO;
import com.example.pblManagement.model.dto.others.DepartmentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {
    DepartmentResponseDTO createDepartment(DepartmentRequestDTO dto);

    DepartmentResponseDTO getDepartmentById(String id);

    DepartmentResponseDTO updateDepartment(String id, DepartmentRequestDTO dto);

    void deleteDepartment(String id);

    Page<DepartmentResponseDTO> getAllDepartments(String search, Pageable pageable);
}
