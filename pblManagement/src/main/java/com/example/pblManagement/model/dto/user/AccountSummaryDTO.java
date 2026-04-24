package com.example.pblManagement.model.dto.user;

import com.example.pblManagement.model.entities.enums.UserRole;
import com.example.pblManagement.model.entities.enums.UserStatus;
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
public abstract class AccountSummaryDTO {
    private String id;

    private String fullName;

    private String email;

}
