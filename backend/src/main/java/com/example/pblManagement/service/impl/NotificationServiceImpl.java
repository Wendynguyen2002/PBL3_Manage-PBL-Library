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

import org.springframework.data.domain.Pageable;
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
    public List<NotificationResponseDTO> getNotifications(Account account, Pageable pageable) {
        List<Notification> notifications = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(account.getId(), pageable);

        return notifications.stream()
                .map(notificationMapper::toResponseDTO)
                .toList();

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
    public void markAllAsRead(Account account) {
        notificationRepository.markAllAsRead(account.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public long getUnreadCount(Account account) {
        return notificationRepository.countByUserIdAndIsReadFalse(account.getId());
    }

    @Override
    public void deleteNotifications(Account account, List<String> notificationIds) {
        notificationRepository.deleteByIds(account.getId(), notificationIds);
    }

    @Override
    public void deleteAllRead(Account account) {
        notificationRepository.deleteAllRead(account.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public long getTotalCount(Account account) {
        return notificationRepository.countByUserId(account.getId());
    }
}
