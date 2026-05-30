package com.example.pblManagement.model.entities;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Student extends Account{
    @Column(length = 50)
    private String homeClass; // Ex: 24T_DT2

    // Many students study the same major
    @ManyToOne
    @JoinColumn(name = "major_id")
    private Major major;

    // One student can be enrolled across different PBL classes
    @Builder.Default
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    @PreRemove
    private void preRemove() {
        if (enrollments != null && !enrollments.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete student enrolled in " + enrollments.size() + " PBL class(es). Please delete corresponding PBL classes first."
            );
        }
    }
}
