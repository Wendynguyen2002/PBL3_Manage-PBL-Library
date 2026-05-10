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

    private String sortBy = "newest"; // newest, highest_rated, most_downloaded

    private int page = 0;

    private int size = 20;
}
