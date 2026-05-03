package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.others.DepartmentRequestDTO;
import com.example.pblManagement.model.dto.others.DepartmentResponseDTO;
import com.example.pblManagement.model.dto.others.DepartmentSummaryDTO;
import com.example.pblManagement.model.entities.Department;
import com.example.pblManagement.service.lookupMappers.LookupLecturerMapper;
import com.example.pblManagement.service.lookupMappers.LookupMajorMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {LookupLecturerMapper.class, LookupMajorMapper.class})
public interface DepartmentMapper {
    Department toEntity(DepartmentRequestDTO dto);

    @Mapping(target = "majorName", source = "majors", qualifiedByName = "mapMajorName")
    @Mapping(target = "lecturerName", source = "lecturers", qualifiedByName = "mapLecturerName")
    DepartmentResponseDTO toResponseDTO(Department department);

    DepartmentSummaryDTO toSummaryDTO(Department department);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    void updateDepartment(@MappingTarget Department department, DepartmentRequestDTO dto);
}
