package com.example.pblManagement.model.dto.progress_task;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
// Details of a progress task - visible to anyone
public class ProgressTaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
}
