package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.pbl.PblClassRequestDTO;
import com.example.pblManagement.model.dto.pbl.PblClassResponseDTO;
import com.example.pblManagement.model.dto.pbl.PblClassSummaryDTO;
import com.example.pblManagement.model.entities.PblClass;
import com.example.pblManagement.service.lookupMappers.LookupLecturerMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {LookupLecturerMapper.class})
public interface PblClassMapper {
    @Mapping(target = "lecturer", source = "lecturerId", qualifiedByName = "mapLecturer")
    PblClass toEntity(PblClassRequestDTO dto);

    @Mapping(target = "lecturerName", source = "lecturer", qualifiedByName = "mapLecturerName")
    PblClassResponseDTO toResponseDTO(PblClass pblClass);

    @Mapping(target = "lecturerName", source = "lecturer", qualifiedByName = "mapLecturerName")
    PblClassSummaryDTO toSummaryDTO(PblClass pblClass);

    @Mapping(target = "lecturer", source = "lecturerId", qualifiedByName = "mapLecturer")
    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    void updatePblClass(@MappingTarget PblClass pblClass, PblClassRequestDTO dto);

}
