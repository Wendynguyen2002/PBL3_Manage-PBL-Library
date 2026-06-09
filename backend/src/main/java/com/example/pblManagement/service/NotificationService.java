package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.common.NotificationResponseDTO;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.model.entities.enums.UserRole;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {
    void createNotification(String userId, UserRole userRole, String title, String message, String type, String referenceId);

    List<NotificationResponseDTO> getNotifications(Account account, Pageable pageable);

    List<NotificationResponseDTO> getUnreadNotifications(Account account);

    void markAllAsRead(Account account);

    long getUnreadCount(Account account);

    void deleteNotifications(Account account, List<String> notificationIds);

    void deleteAllRead(Account account);

    long getTotalCount(Account account);
}
