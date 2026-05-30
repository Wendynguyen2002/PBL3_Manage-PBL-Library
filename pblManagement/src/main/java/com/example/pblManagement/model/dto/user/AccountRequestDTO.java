package com.example.pblManagement.model.dto.user;

import com.example.pblManagement.model.entities.enums.Gender;
import com.example.pblManagement.model.entities.enums.UserRole;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AccountRequestDTO {
    @NotBlank(message = "ID is required")
    @Pattern(regexp = "^[0-9]{4,20}$", message = "ID must be in 4-20 digits")
    private String id;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    private Gender gender;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    private String homeTown;

    @NotNull(message = "Role is required")
    private UserRole role;
}
