package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.user.StudentRequestDTO;
import com.example.pblManagement.model.dto.user.StudentResponseDTO;
import com.example.pblManagement.model.dto.user.StudentSelfUpdateRequestDTO;
import com.example.pblManagement.model.dto.user.StudentSummaryDTO;
import com.example.pblManagement.model.entities.Major;
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
    @Mapping(target = "email", ignore = true)  // Prevent changing email
    @Mapping(target = "password", ignore = true) // Prevent changing password here (use separate endpoint for password changes)
    @Mapping(target = "id", ignore = true)  // Prevent changing IDs
    void updateStudent(@MappingTarget Student student, StudentRequestDTO dto);

    // Only extract 1 major name
    @Named("mapMajorName")
    default String mapMajorName(Major major) {
        if (major == null) return null;
        return major.getName();
    }

    @Named("mapToStudentSummary")
    default StudentSummaryDTO mapToStudentSummary(Student student) {
        if (student == null) return null;
        return StudentSummaryDTO.builder()
                .id(student.getId())
                .fullName(student.getFullName())
                .homeClass(student.getHomeClass())
                .build();
    }
}
