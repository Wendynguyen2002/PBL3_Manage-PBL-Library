package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.finalreport.FinalReportRequestDTO;
import com.example.pblManagement.model.dto.finalreport.FinalReportResponseDTO;
import com.example.pblManagement.model.dto.finalreport.FinalReportSummaryDTO;
import com.example.pblManagement.model.entities.FinalReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {StudentMapper.class})
public interface FinalReportMapper {
    FinalReport toEntity(FinalReportRequestDTO dto);

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", source = "group.groupName")
    @Mapping(target = "submittedBy", source = "submittedBy", qualifiedByName = "mapToStudentSummary")
    @Mapping(target = "lastModifiedBy", source = "lastModifiedBy", qualifiedByName = "mapToStudentSummary")
    FinalReportResponseDTO toResponseDTO(FinalReport report);

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", source = "group.groupName")
    @Mapping(target = "submittedByStudentName", source = "submittedBy.fullName")
    FinalReportSummaryDTO toSummaryDTO(FinalReport report);
}
