package com.example.pblManagement.model.dto.pbl;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PblClassRequestDTO {
    @NotBlank(message = "Class ID is required")
    private String id;

    @NotBlank(message = "Class name is required")
    @Size(min = 3, max = 100, message = "Class name must be between 3 and 100 characters")
    private String className;

    @NotBlank(message = "Semester is required")
    @Pattern(regexp = "^(Spring|Summer|Fall)\\s[0-9]{4}$|^[0-9]{4}-[0-9]{4}$",
            message = "Semester format: 'Spring 2024', 'Summer 2024', 'Fall 2024', or '2024-2025'")
    private String semester;

    @NotNull(message = "Maximum students per group is required")
    @Min(value = 1, message = "Maximum students per group must be at least 1")
    @Max(value = 10, message = "Maximum students per group cannot exceed 10")
    private Integer maxStudentsPerGroup;

    @NotNull(message = "Lecturer ID is required")
    private String lecturerId;

}
