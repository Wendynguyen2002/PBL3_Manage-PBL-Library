package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.common.NotificationResponseDTO;
import com.example.pblManagement.model.entities.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "isRead", source = "read")
    NotificationResponseDTO toResponseDTO(Notification notification);
}
