package com.example.pblManagement.model.dto.progress_task;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskSubmissionRequestDTO {
    @NotBlank(message = "Brief description is required")
    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
    private String briefDescription;

    @Valid
    @Builder.Default
    private List<SubmissionLinkRequestDTO> links = new ArrayList<>();
}
