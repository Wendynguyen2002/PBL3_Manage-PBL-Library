package com.example.pblManagement.model.dto.others;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MajorRequestDTO {
    @NotBlank(message = "Major ID is required")
    private String id;

    @NotBlank(message = "Major name is required")
    @Size(min = 2, max = 100, message = "Major name must be between 2 and 100 characters")
    private String name;

    @NotBlank
    private String departmentId;
}
