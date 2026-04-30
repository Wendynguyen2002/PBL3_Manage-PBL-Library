package com.example.pblManagement.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "group_memberships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "pbl_group_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Junction table, sits between PblGroup and Students to solve many-to-many relationship
public class GroupMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // See student.java
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "pbl_group_id", nullable = false)
    private PblGroup pblGroup;

}
