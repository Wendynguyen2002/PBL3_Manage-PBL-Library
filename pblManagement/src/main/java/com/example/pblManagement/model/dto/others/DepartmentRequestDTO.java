package com.example.pblManagement.model.dto.others;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentRequestDTO {
    @NotBlank(message = "ID is required")
    private String id;

    @NotBlank(message = "Department name is required")
    private String name;
}
