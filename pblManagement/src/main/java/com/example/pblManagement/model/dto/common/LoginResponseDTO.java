package com.example.pblManagement.model.dto.common;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private String Id;
    private String role;
    private String message;
    private String email;
}
