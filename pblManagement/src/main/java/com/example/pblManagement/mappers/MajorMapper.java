package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.others.MajorRequestDTO;
import com.example.pblManagement.model.dto.others.MajorSummaryDTO;
import com.example.pblManagement.model.entities.Major;
import com.example.pblManagement.service.lookupMappers.LookupDepartmentMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {LookupDepartmentMapper.class})
public interface MajorMapper {
    @Mapping(target = "department", source = "departmentId", qualifiedByName = "mapDepartment")
    Major toEntity(MajorRequestDTO dto);

    @Mapping(target = "departmentName", source = "department", qualifiedByName = "mapDepartmentName")
    MajorSummaryDTO toSummaryDTO(Major major);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "department", source = "departmentId", qualifiedByName = "mapDepartment")
    void updateMajor(@MappingTarget Major major, MajorRequestDTO dto);
}
