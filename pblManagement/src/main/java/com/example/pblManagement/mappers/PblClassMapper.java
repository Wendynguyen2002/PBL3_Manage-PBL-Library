package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.pbl.PblClassRequestDTO;
import com.example.pblManagement.model.dto.pbl.PblClassResponseDTO;
import com.example.pblManagement.model.dto.pbl.PblClassSummaryDTO;
import com.example.pblManagement.model.entities.PblClass;
import com.example.pblManagement.service.lookupMappers.LookupDepartmentMapper;
import com.example.pblManagement.service.lookupMappers.LookupLecturerMapper;
import com.example.pblManagement.service.lookupMappers.LookupMajorMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {LookupLecturerMapper.class, LookupMajorMapper.class})
public interface PblClassMapper {
    @Mapping(target = "lecturerName", source = "lecturer", qualifiedByName = "mapLecturerName")
    @Mapping(target = "majorNames", source = "majors", qualifiedByName = "mapMajorNames")
    PblClassResponseDTO toResponseDTO(PblClass pblClass);

    @Mapping(target = "lecturerName", source = "lecturer", qualifiedByName = "mapLecturerName")
    @Mapping(target = "majorNames", source = "majors", qualifiedByName = "mapMajorNames")
    PblClassSummaryDTO toSummaryDTO(PblClass pblClass);
}
