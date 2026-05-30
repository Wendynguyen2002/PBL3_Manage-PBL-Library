package com.example.pblManagement.model.dto.project;

import com.example.pblManagement.model.entities.enums.ProjectStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectSummaryDTO {
    private Long id;

    private String title;

    private ProjectStatus projectStatus;
}
