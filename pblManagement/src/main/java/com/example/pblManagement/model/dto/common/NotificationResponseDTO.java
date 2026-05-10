package com.example.pblManagement.model.dto.common;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {
    private String id;
    private String title;
    private String message;
    private String type;
    private String referenceId;
    private boolean isRead;
    private LocalDateTime createdAt;
}
