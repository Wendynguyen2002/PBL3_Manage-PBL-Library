package com.example.pblManagement.model.dto.user;

import com.example.pblManagement.model.entities.enums.Gender;
import com.example.pblManagement.model.entities.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AccountResponseDTO {
    private String id;

    private String fullName;

    private String email;

    private Gender gender;

    private LocalDate dateOfBirth;

    private UserRole role;

    private String phoneNumber;

    private String homeTown;
}
