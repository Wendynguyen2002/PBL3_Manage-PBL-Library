package com.example.pblManagement.model.dto.progress_task;

import com.example.pblManagement.model.dto.user.StudentSummaryDTO;
import com.example.pblManagement.model.entities.enums.TaskSubmissionStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskSubmissionResponseDTO {
    private Long id;
    private String briefDescription;
    private LocalDateTime submittedAt;
    private LocalDateTime lastModifiedAt;
    private Boolean isLate;
    private TaskSubmissionStatus status;

    // Who submitted
    private StudentSummaryDTO submittedBy;
    private StudentSummaryDTO lastModifiedBy;

    // Group info
    private Long groupId;
    private String groupName;

    // Links
    private List<SubmissionLinkResponseDTO> links;
}
