package com.example.pblManagement.model.dto.progress_task;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Available tasks - visible to everyone
public class ProgressTaskSummaryDTO {
    private Long id;

    private String title;

    private LocalDateTime dueDate;
}
