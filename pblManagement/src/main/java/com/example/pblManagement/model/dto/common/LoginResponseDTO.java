package com.example.pblManagement.model.dto.common;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private String id;

    private String email;

    private String role;

    private String message;
}
