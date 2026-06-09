package com.example.pblManagement.model.dto.finalreport;

import com.example.pblManagement.model.dto.user.StudentSummaryDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalReportResponseDTO {
    private Long id;

    private String title;

    private String description;

    private String filePath;

    private String fileType;

    private String originalFileName;

    private LocalDateTime submittedAt;

    private LocalDateTime lastModifiedAt;

    private Long groupId;

    private String groupName;

    private StudentSummaryDTO submittedBy;

    private StudentSummaryDTO lastModifiedBy;
}
