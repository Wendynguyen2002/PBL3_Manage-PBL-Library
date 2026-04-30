package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.pbl.PblGroupSummaryDTO;
import com.example.pblManagement.model.entities.PblGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PblGroupMapper {
    // For PblGroupSummaryDTO, we will manually set the projectTitle and groupName in the service layer,
    // and members will be mapped from the list of Student entities to StudentSummaryDTOs.
    @Mapping(target = "currentMemberCount", expression = "java(group.getCurrentMemberCount())")
    @Mapping(target = "isFull", expression = "java(group.isFull())")
    PblGroupSummaryDTO toSummaryDTO(PblGroup group);

    List<PblGroupSummaryDTO> toSummaryDTOList(List<PblGroup> groups);
}
