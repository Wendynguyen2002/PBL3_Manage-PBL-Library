package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.user.StudentRequestDTO;
import com.example.pblManagement.model.dto.user.StudentResponseDTO;
import com.example.pblManagement.model.dto.user.StudentSelfUpdateRequestDTO;
import com.example.pblManagement.model.dto.user.StudentSummaryDTO;
import com.example.pblManagement.model.entities.Student;
import com.example.pblManagement.service.lookupMappers.LookupMajorMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {LookupMajorMapper.class})
public interface StudentMapper {
    @Mapping(target = "major", source = "majorId", qualifiedByName = "mapMajor")
    Student toEntity(StudentRequestDTO dto);

    @Mapping(target = "majorName", source = "major", qualifiedByName = "mapMajorName")
    StudentResponseDTO toResponseDTO(Student student);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSelf(@MappingTarget Student student, StudentSelfUpdateRequestDTO dto);

    StudentSummaryDTO toSummaryDTO(Student student);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "major", source = "majorId", qualifiedByName = "mapMajor")
    void updateStudent(@MappingTarget Student student, StudentRequestDTO dto);
}
