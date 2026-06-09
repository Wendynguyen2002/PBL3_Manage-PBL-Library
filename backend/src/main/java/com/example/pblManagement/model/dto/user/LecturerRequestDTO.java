package com.example.pblManagement.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LecturerRequestDTO extends AccountRequestDTO {
    @Size(max = 100, message = "Specialization must not exceed 100 characters")
    private String specialization;

    @Size(max = 50, message = "Position must not exceed 50 characters")
    private String position;

    @Size(max = 50, message = "Degree must not exceed 50 characters")
    private String degree;

    @NotBlank(message = "Department is required")
    private String departmentId;
}
