package com.example.pblManagement.model.dto.progress_task;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupTaskStatusDTO {
    private ProgressTaskSummaryDTO task;
    private List<TaskSubmissionSummaryDTO> groupSubmissions;
}
