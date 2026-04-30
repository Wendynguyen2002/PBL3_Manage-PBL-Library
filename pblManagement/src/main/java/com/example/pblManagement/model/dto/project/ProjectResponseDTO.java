package com.example.pblManagement.model.dto.project;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String status;  // AVAILABLE, TAKEN
}
