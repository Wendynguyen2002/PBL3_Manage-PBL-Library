package com.example.pblManagement.service.impl;

import com.example.pblManagement.mappers.NotificationMapper;
import com.example.pblManagement.model.dto.common.NotificationResponseDTO;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.model.entities.Notification;
import com.example.pblManagement.model.entities.enums.UserRole;
import com.example.pblManagement.repositories.NotificationRepository;
import com.example.pblManagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public void createNotification(String userId, UserRole userRole, String title, String message, String type, String referenceId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .userRole(userRole)
                .title(title)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    @Override
    public List<NotificationResponseDTO> getUnreadNotifications(Account account) {
        List<Notification> notifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(account.getId());
        return notifications.stream()
                .map(notificationMapper::toResponseDTO)
                .toList();
    }

    @Override
    public void markAsRead(Account account, List<String> notificationIds) {
        notificationRepository.markAsRead(account.getId(), notificationIds);
    }

    @Transactional(readOnly = true)
    @Override
    public long getUnreadCount(Account account) {
        return notificationRepository.countByUserIdAndIsReadFalse(account.getId());
    }
}
