package com.example.pblManagement.model.dto.common;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarkNotificationReadRequestDTO {
    private List<String> notificationIds;
}
