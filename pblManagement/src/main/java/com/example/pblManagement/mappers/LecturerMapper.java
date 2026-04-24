package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.user.LecturerRequestDTO;
import com.example.pblManagement.model.dto.user.LecturerResponseDTO;
import com.example.pblManagement.model.dto.user.LecturerSelfUpdateRequestDTO;
import com.example.pblManagement.model.dto.user.LecturerSummaryDTO;
import com.example.pblManagement.model.entities.Lecturer;
import com.example.pblManagement.service.lookupMappers.DepartmentMapperForLecturer;
import com.example.pblManagement.service.lookupMappers.PblClassMapperForLecturer;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {DepartmentMapperForLecturer.class, PblClassMapperForLecturer.class})
public interface LecturerMapper {
    @Mapping(target = "department", source = "departmentId", qualifiedByName = "mapDepartment")
    Lecturer toEntity(LecturerRequestDTO dto);

    @Mapping(target = "pblClassIds", source = "pblClasses", qualifiedByName = "mapPblClassIds")
    @Mapping(target = "pblClassNames", source = "pblClasses", qualifiedByName = "mapPblClassNames")
    LecturerResponseDTO toResponseDTO(Lecturer lecturer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSelf(@MappingTarget Lecturer lecturer, LecturerSelfUpdateRequestDTO dto);

    LecturerSummaryDTO toSummaryDTO(Lecturer lecturer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", source = "departmentId", qualifiedByName = "mapDepartment")
    void updateLecturer(@MappingTarget Lecturer lecturer, LecturerRequestDTO dto);
}
