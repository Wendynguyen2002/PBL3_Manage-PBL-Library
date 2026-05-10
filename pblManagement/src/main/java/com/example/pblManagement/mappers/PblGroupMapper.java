package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.pbl.PblGroupSummaryDTO;
import com.example.pblManagement.model.dto.user.StudentSummaryDTO;
import com.example.pblManagement.model.entities.Enrollment;
import com.example.pblManagement.model.entities.PblGroup;
import com.example.pblManagement.model.entities.Project;
import com.example.pblManagement.model.entities.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PblGroupMapper {
    @Mapping(target = "projectTitle", source = "project", qualifiedByName = "mapProjectToTitle")
    @Mapping(target = "members", source = "enrollments", qualifiedByName = "mapEnrollmentsToStudentSummaries")
    @Mapping(target = "currentMemberCount", expression = "java(group.getEnrollments() != null ? group.getEnrollments().size() : 0)")
    @Mapping(target = "isFull", expression = "java(PblGroupMapper.isGroupFull(group))")
    PblGroupSummaryDTO toSummaryDTO(PblGroup group);

    @Named("mapProjectToTitle")
    default String mapProjectToTitle(Project project) {
        return project != null ? project.getTitle() : null;
    }

    @Named("mapEnrollmentsToStudentSummaries")
    default List<StudentSummaryDTO> mapEnrollmentsToStudentSummaries(List<Enrollment> enrollments) {
        if (enrollments == null) return List.of();
        return enrollments.stream()
                .map(this::enrollmentToStudentSummary)
                .toList();
    }

    // Static helper method for isFull calculation
    static boolean isGroupFull(PblGroup group) {
        if (group == null || group.getPblClass() == null) return true;
        Integer max = group.getPblClass().getMaxStudentsPerGroup();
        if (max == null) return true;
        int currentCount = group.getEnrollments() != null ? group.getEnrollments().size() : 0;
        return currentCount >= max;
    }

    // Helper method for converting single enrollment to student summary
    default StudentSummaryDTO enrollmentToStudentSummary(Enrollment enrollment) {
        if (enrollment == null) return null;
        Student student = enrollment.getStudent();
        return StudentSummaryDTO.builder()
                .id(student.getId())
                .fullName(student.getFullName())
                .homeClass(student.getHomeClass())
                .build();
    }
}
