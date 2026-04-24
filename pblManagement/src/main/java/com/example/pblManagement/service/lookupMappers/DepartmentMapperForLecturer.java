package com.example.pblManagement.service.lookupMappers;

import com.example.pblManagement.model.entities.Department;
import com.example.pblManagement.repositories.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepartmentMapperForLecturer {
    private final DepartmentRepository departmentRepository;

    @Named("mapDepartment")
    public Department mapDepartment(String departmentId) {
        if (departmentId == null) return null;
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Department not found: " + departmentId));
    }
}
