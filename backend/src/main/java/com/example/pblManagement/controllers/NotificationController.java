package com.example.pblManagement.controllers;

import com.example.pblManagement.model.dto.common.NotificationResponseDTO;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.security.CurrentUser;
import com.example.pblManagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // All roles: See all notifications
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getNotifications(
            @CurrentUser Account account,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(notificationService.getNotifications(account, pageable));
    }

    // All roles: Get total count for pagination info
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalCount(@CurrentUser Account account) {
        return ResponseEntity.ok(notificationService.getTotalCount(account));
    }

    // All roles: List all unread notifications, for bell icon dropdown
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotifications(@CurrentUser Account account) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(account));
    }

    // All roles: Get unread count for bell icon badge
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(@CurrentUser Account account) {
        return ResponseEntity.ok(notificationService.getUnreadCount(account));
    }

    // All roles: Mark all notifications as read
    @PutMapping("/mark-all-as-read")
    public ResponseEntity<Void> markAllAsRead(@CurrentUser Account account) {
        notificationService.markAllAsRead(account);
        return ResponseEntity.ok().build();
    }

    // All roles: Delete multiple notifications
    @DeleteMapping
    public ResponseEntity<Void> deleteNotifications(
            @CurrentUser Account account,
            @RequestBody List<String> notificationIds) {
        notificationService.deleteNotifications(account, notificationIds);
        return ResponseEntity.ok().build();
    }

    // All roles: Delete all read notifications (cleanup)
    @DeleteMapping("/read")
    public ResponseEntity<Void> deleteAllRead(@CurrentUser Account account) {
        notificationService.deleteAllRead(account);
        return ResponseEntity.ok().build();
    }
}
