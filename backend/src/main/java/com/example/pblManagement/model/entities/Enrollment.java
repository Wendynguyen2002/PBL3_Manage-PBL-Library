package com.example.pblManagement.model.entities;

import com.example.pblManagement.model.entities.enums.StudentGroupStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "class_enrollments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"student_id", "pbl_class_id"}) // only one record
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Junction table between Student and PblClass, with optional link to PblGroup
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "pbl_group_id") // nullable - student may not be in a group
    private PblGroup pblGroup;

    @ManyToOne
    @JoinColumn(name = "pbl_class_id", nullable = false)
    private PblClass pblClass;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudentGroupStatus status;  // IN_GROUP or NOT_IN_GROUP
}
