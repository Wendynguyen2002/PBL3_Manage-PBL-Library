package com.example.pblManagement.model.dto.finalreport;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Only lecturer gets to see this
public class FinalReportSummaryDTO {
    private Long id;

    private String title;

    private Long groupId;

    private String groupName;

    private String submittedByStudentName;

    private LocalDateTime submittedAt;

    private String fileType;
}
