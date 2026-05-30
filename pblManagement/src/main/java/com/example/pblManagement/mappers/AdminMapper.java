package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.user.AdminResponseDTO;
import com.example.pblManagement.model.entities.Admin;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    AdminResponseDTO toResponseDTO(Admin admin);

}
