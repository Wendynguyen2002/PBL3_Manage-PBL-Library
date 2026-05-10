package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(String userId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId AND n.id IN :notificationIds")
    void markAsRead(String userId, List<String> notificationIds);

    long countByUserIdAndIsReadFalse(String userId);
}
