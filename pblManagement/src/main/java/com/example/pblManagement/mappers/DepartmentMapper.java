package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.others.DepartmentRequestDTO;
import com.example.pblManagement.model.dto.others.DepartmentResponseDTO;
import com.example.pblManagement.model.dto.others.DepartmentSummaryDTO;
import com.example.pblManagement.model.entities.Department;
import com.example.pblManagement.model.entities.Lecturer;
import com.example.pblManagement.model.entities.Major;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    Department toEntity(DepartmentRequestDTO dto);

    @Mapping(target = "majorNames", source = "majors", qualifiedByName = "mapMajorNames")
    @Mapping(target = "lecturerNames", source = "lecturers", qualifiedByName = "mapLecturerNames")
    DepartmentResponseDTO toResponseDTO(Department department);

    DepartmentSummaryDTO toSummaryDTO(Department department);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    void updateDepartment(@MappingTarget Department department, DepartmentRequestDTO dto);

    // Extract many lecturers' names
    @Named("mapLecturerNames")
    default List<String> mapLecturerNames(List<Lecturer> lecturers) {
        if (lecturers == null) return List.of();
        return lecturers.stream()
                .map(Lecturer::getFullName)
                .toList();
    }

    // Extract many majors' names
    @Named("mapMajorNames")
    default List<String> mapMajorNames(List<Major> majors) {
        if (majors == null) return List.of();
        return majors.stream()
                .map(Major::getName)
                .toList();
    }
}
