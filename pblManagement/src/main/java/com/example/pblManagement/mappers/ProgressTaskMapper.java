package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.progress_task.ProgressTaskRequestDTO;
import com.example.pblManagement.model.dto.progress_task.ProgressTaskResponseDTO;
import com.example.pblManagement.model.dto.progress_task.ProgressTaskSummaryDTO;
import com.example.pblManagement.model.entities.ProgressTask;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProgressTaskMapper {
    ProgressTask toEntity(ProgressTaskRequestDTO dto);

    ProgressTaskResponseDTO toResponseDTO(ProgressTask progressTask);

    ProgressTaskSummaryDTO toSummaryDTO(ProgressTask progressTask);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProgressTask(@MappingTarget ProgressTask progressTask, ProgressTaskRequestDTO dto);
}
