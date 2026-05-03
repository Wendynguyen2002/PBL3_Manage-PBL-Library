package com.example.pblManagement.model.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LecturerSelfUpdateRequestDTO {
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    private String homeTown;

    @Size(max = 100, message = "Specialization must not exceed 100 characters")
    private String specialization;

    @Size(max = 50, message = "Position must not exceed 50 characters")
    private String position;

    @Size(max = 50, message = "Degree must not exceed 50 characters")
    private String degree;
}
