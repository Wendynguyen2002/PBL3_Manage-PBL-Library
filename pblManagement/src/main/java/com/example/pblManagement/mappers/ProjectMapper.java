package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.project.AvailableProjectsDTO;
import com.example.pblManagement.model.dto.project.ProjectRequestDTO;
import com.example.pblManagement.model.dto.project.ProjectResponseDTO;
import com.example.pblManagement.model.dto.project.ProjectSummaryDTO;
import com.example.pblManagement.model.entities.Project;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    Project toEntity(ProjectRequestDTO dto);

    ProjectResponseDTO toResponseDTO(Project project);

    ProjectSummaryDTO toSummaryDTO(Project project);

    AvailableProjectsDTO toAvailableSummary(Project project);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    void updateProject(@MappingTarget Project project, ProjectRequestDTO dto);
}
