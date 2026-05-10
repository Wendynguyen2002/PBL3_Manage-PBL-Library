package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.progress_task.TaskSubmissionRequestDTO;
import com.example.pblManagement.model.dto.progress_task.TaskSubmissionResponseDTO;
import com.example.pblManagement.model.dto.progress_task.TaskSubmissionSummaryDTO;
import com.example.pblManagement.model.entities.TaskSubmission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {StudentMapper.class})
public interface TaskSubmissionMapper {
    TaskSubmission toEntity(TaskSubmissionRequestDTO dto);

    @Mapping(target = "submittedBy", source = "submittedBy", qualifiedByName = "mapToStudentSummary")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy", qualifiedByName = "mapToStudentSummary")
    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", source = "group.groupName")
    TaskSubmissionResponseDTO toResponseDTO(TaskSubmission submission);

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", source = "group.groupName")
    @Mapping(target = "hasSubmitted", source = "id", qualifiedByName = "mapHasSubmitted")
    @Mapping(target = "submittedByStudentName", source = "submittedBy.fullName")
    TaskSubmissionSummaryDTO toSummaryDTO(TaskSubmission submission);

    @Named("mapHasSubmitted")
    default boolean mapHasSubmitted(Long id) {
        return id != null;
    }
}
