package com.example.pblManagement.exceptions;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationErrorResponseDTO {
    private LocalDateTime timestamp;
    private Integer status;
    private String message;
    private Map<String, String> errors;
}
