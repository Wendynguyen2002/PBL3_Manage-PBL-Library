package com.example.pblManagement.model.dto.others;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MajorRequestDTO {
    @NotBlank(message = "Major ID is required")
    @Pattern(regexp = "^[A-Z0-9]{3,10}$",
            message = "Major ID must contain 3-10 uppercase letters and numbers only")
    private String id;

    @NotBlank(message = "Major name is required")
    @Size(min = 2, max = 100, message = "Major name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s\\-&.,]+$",
            message = "Major name can only contain letters, spaces, hyphens, ampersands, periods, and commas")
    private String name;

    private String departmentId;
}
