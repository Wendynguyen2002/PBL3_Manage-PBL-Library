package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.common.NotificationResponseDTO;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.model.entities.enums.UserRole;

import java.util.List;

public interface NotificationService {
    void createNotification(String userId, UserRole userRole, String title, String message, String type, String referenceId);

    List<NotificationResponseDTO> getUnreadNotifications(Account account);

    void markAsRead(Account account, List<String> notificationIds);

    long getUnreadCount(Account account);
}
