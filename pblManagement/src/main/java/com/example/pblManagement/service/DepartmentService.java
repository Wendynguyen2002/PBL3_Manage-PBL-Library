package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.others.DepartmentRequestDTO;
import com.example.pblManagement.model.dto.others.DepartmentResponseDTO;
import com.example.pblManagement.model.dto.others.DepartmentSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepartmentService {
    DepartmentResponseDTO createDepartment(DepartmentRequestDTO dto);

    DepartmentResponseDTO getDepartmentById(String id);

    DepartmentResponseDTO updateDepartment(String id, DepartmentRequestDTO dto);

    void deleteDepartment(String id);

    Page<DepartmentSummaryDTO> getAllDepartments(String search, Pageable pageable);

    List<DepartmentSummaryDTO> getAllDepartmentsForDropdown();
}
