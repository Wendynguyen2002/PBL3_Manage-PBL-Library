package com.example.pblManagement.model.entities;

import com.example.pblManagement.model.entities.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;  // "Added to PBL Class"

    @Column(nullable = false)
    private String userId;  // Store the ID (works for Admin, Lecturer, or Student)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;  // Store role for optional filtering

    @Column(nullable = false, length = 500)
    private String message;  // "You have been added to class 24Nh11"

    @Column(nullable = false)
    private String type;  // "CLASS_ENROLLMENT", "CLASS_REMOVAL", "GROUP_FORMED", etc.

    @Column(nullable = false)
    private String referenceId;  // pblClassId or pblGroupId, etc.

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
