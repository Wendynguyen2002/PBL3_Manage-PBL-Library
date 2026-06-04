package com.example.pblManagement.model.dto.progress_task;

import com.example.pblManagement.model.entities.enums.TaskSubmissionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Only lecturer can see this
public class TaskSubmissionSummaryDTO {
    private Long groupId;

    private String groupName;

    private boolean hasSubmitted;

    private LocalDateTime submittedAt;

    private Boolean isLate;

    private TaskSubmissionStatus status;

    private String submittedByStudentName;
}
