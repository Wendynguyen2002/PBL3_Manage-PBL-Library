package com.example.pblManagement.model.entities;

import com.example.pblManagement.model.entities.enums.Gender;
import com.example.pblManagement.model.entities.enums.UserRole;
import com.example.pblManagement.model.entities.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
// Account in general used for students, lecturers and administrators
public abstract class Account {
    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role; // ADMIN, LECTURER, STUDENT

    @Column(length = 15, unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status; // ACTIVATED, DEACTIVATED

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private String homeTown;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.status == null) {
            this.status = UserStatus.ACTIVATED;
        }
    }
}
