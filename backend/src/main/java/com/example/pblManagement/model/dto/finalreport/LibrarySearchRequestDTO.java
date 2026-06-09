package com.example.pblManagement.model.dto.finalreport;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibrarySearchRequestDTO {
    private String keyword;           // Search in title + description

    private String className;         // Filter by class name (partial match)

    private String departmentId;      // Filter by department

    private String fileType;          // PDF, DOCX, PPT

    @Builder.Default
    private String sortBy = "newest"; // newest, highest_rated, most_downloaded

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 20;
}
