package com.example.pblManagement.mappers;

import com.example.pblManagement.model.dto.progress_task.SubmissionLinkRequestDTO;
import com.example.pblManagement.model.dto.progress_task.SubmissionLinkResponseDTO;
import com.example.pblManagement.model.entities.SubmissionLink;
import com.example.pblManagement.model.entities.enums.LinkType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SubmissionLinkMapper {
    @Mapping(target = "linkType", source = "url", qualifiedByName = "detectLinkType")
    SubmissionLink toEntity(SubmissionLinkRequestDTO dto);

    SubmissionLinkResponseDTO toResponseDTO(SubmissionLink submissionLink);

    @Named("detectLinkType")
    default LinkType detectLinkType(String url) {
        if (url == null) return LinkType.OTHER;
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.contains("github.com")) {
            return LinkType.GITHUB;
        } else if (lowerUrl.contains("drive.google.com")) {
            return LinkType.GOOGLE_DRIVE;
        }
        return LinkType.OTHER;
    }
}
