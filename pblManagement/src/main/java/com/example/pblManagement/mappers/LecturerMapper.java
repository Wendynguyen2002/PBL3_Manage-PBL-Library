package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.user.LecturerRequestDTO;
import com.example.pblManagement.model.dto.user.LecturerResponseDTO;
import com.example.pblManagement.model.dto.user.LecturerSelfUpdateRequestDTO;
import com.example.pblManagement.model.dto.user.LecturerSummaryDTO;
import com.example.pblManagement.model.entities.Department;
import com.example.pblManagement.model.entities.Lecturer;
import com.example.pblManagement.model.entities.PblClass;
import com.example.pblManagement.service.lookupMappers.LookupDepartmentMapper;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {LookupDepartmentMapper.class})
public interface LecturerMapper {
    @Mapping(target = "department", source = "departmentId", qualifiedByName = "mapDepartment")
    Lecturer toEntity(LecturerRequestDTO dto);

    @Mapping(target = "pblClassNames", source = "pblClasses", qualifiedByName = "mapPblClassNames")
    @Mapping(target = "pblClassSemesters", source = "pblClasses", qualifiedByName = "mapPblClassSemesters")
    @Mapping(target = "departmentName", source = "department", qualifiedByName = "mapDepartmentName")
    LecturerResponseDTO toResponseDTO(Lecturer lecturer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSelf(@MappingTarget Lecturer lecturer, LecturerSelfUpdateRequestDTO dto);

    @Mapping(target = "departmentName", source = "department", qualifiedByName = "mapDepartmentName")
    LecturerSummaryDTO toSummaryDTO(Lecturer lecturer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "department", source = "departmentId", qualifiedByName = "mapDepartment")
    @Mapping(target = "email", ignore = true)  // Prevent changing email
    @Mapping(target = "password", ignore = true) // Prevent changing password here (use separate endpoint for password changes)
    @Mapping(target = "id", ignore = true)  // Prevent changing IDs
    void updateLecturer(@MappingTarget Lecturer lecturer, LecturerRequestDTO dto);

    // Extract 1 department name
    @Named("mapDepartmentName")
    default String mapDepartmentName(Department department) {
        if (department == null) return null;
        return department.getName();
    }

    // Extract PBL classes' names
    @Named("mapPblClassNames")
    default List<String> toNames(List<PblClass> pblClasses) {
        if (pblClasses == null) return List.of();
        return pblClasses.stream()
                .map(PblClass::getClassName)
                .collect(Collectors.toList());
    }

    // Extract PBL classes' semesters
    @Named("mapPblClassSemesters")
    default List<String> toSemesters(List<PblClass> pblClasses) {
        if (pblClasses == null) return List.of();
        return pblClasses.stream()
                .map(PblClass::getSemester)
                .collect(Collectors.toList());
    }
}
