package com.example.pblManagement.model.dto.finalreport;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryReportResponseDTO {
    private Long id;

    private String title;

    private String description;

    private String fileType;

    private String className;

    private String departmentName;

    private Double averageRating;

    private Integer ratingCount;

    private Integer downloadCount;

    private LocalDateTime submittedAt;
    // No publisher name (anonymous)
}
