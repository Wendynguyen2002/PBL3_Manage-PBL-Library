package com.example.pblManagement.service.lookupMappers;

import com.example.pblManagement.model.entities.Department;
import com.example.pblManagement.repositories.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LookupDepartmentMapper {
    private final DepartmentRepository departmentRepository;

    // On creating a lecturer, assign a department
    @Named("mapDepartment")
    public Department mapDepartment(String departmentId) {
        if (departmentId == null) return null;
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Department not found: " + departmentId));
    }

    // Only extract the department name
    @Named("mapDepartmentName")
    public String mapDepartmentName(Department department) {
        if (department == null) return null;
        return department.getName();
    }
}
