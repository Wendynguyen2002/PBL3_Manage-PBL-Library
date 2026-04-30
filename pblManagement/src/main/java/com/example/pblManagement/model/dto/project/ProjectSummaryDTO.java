package com.example.pblManagement.model.dto.project;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectSummaryDTO {
    private String title;
    private String status;
}
