package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    // Get ALL notifications (with pagination)
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    // Count total notifications for a user
    long countByUserId(String userId);

    // Get unread notifications for badge count
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(String userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.isRead = false")
    void markAllAsRead(String userId);

    long countByUserIdAndIsReadFalse(String userId);

    // Delete specific notifications
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.userId = :userId AND n.id IN :notificationIds")
    void deleteByIds(String userId, List<String> notificationIds);

    // Delete ALL read notifications for a user (cleanup)
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.userId = :userId AND n.isRead = true")
    void deleteAllRead(String userId);
}
