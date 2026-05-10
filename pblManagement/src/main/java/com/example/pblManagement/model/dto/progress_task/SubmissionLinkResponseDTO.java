package com.example.pblManagement.model.dto.progress_task;

import com.example.pblManagement.model.entities.enums.LinkType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionLinkResponseDTO {
    private Long id;
    private String url;
    private LinkType linkType;
}
