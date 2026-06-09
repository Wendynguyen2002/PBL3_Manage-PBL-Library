package com.example.pblManagement.model.entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {
    @Id
    @Column(nullable = false, length = 20)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    // One department has many majors
    @Builder.Default
    @OneToMany(mappedBy = "department")
    private List<Major> majors = new ArrayList<>();

    // One department has many lecturers in it
    @Builder.Default
    @OneToMany(mappedBy = "department")
    private List<Lecturer> lecturers = new ArrayList<>();

    // On removal constraints
    @PreRemove
    private void preRemove() {
        if (majors != null && !majors.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete department with " + majors.size() + " major(s)"
            );
        }
        if (lecturers != null && !lecturers.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete department with " + lecturers.size() + " lecturer(s)"
            );
        }
    }
}
