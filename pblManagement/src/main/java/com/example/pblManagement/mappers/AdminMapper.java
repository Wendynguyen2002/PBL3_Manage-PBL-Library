package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.user.AdminRequestDTO;
import com.example.pblManagement.model.dto.user.AdminResponseDTO;
import com.example.pblManagement.model.dto.user.AdminSummaryDTO;
import com.example.pblManagement.model.entities.Admin;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    Admin toEntity(AdminRequestDTO dto);

    AdminResponseDTO toResponseDTO(Admin admin);

    AdminSummaryDTO toSummaryDTO(Admin admin);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAdmin(@MappingTarget Admin admin, AdminRequestDTO dto);
}
