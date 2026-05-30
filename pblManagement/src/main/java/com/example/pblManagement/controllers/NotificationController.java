package com.example.pblManagement.controllers;

import com.example.pblManagement.model.dto.common.MarkNotificationReadRequestDTO;
import com.example.pblManagement.model.dto.common.NotificationResponseDTO;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.security.CurrentUser;
import com.example.pblManagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // All roles: List all unread notifications
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotifications(@CurrentUser Account account) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(account));
    }

    // All roles: Get unread count for friendly frontend display
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(@CurrentUser Account account) {
        return ResponseEntity.ok(notificationService.getUnreadCount(account));
    }

    // All roles: Mark specific notifications as read
    @PutMapping("/mark-as-read")
    public ResponseEntity<Void> markAsRead(
            @CurrentUser Account account,
            @RequestBody MarkNotificationReadRequestDTO request) {
        notificationService.markAsRead(account, request.getNotificationIds());
        return ResponseEntity.ok().build();
    }
}
