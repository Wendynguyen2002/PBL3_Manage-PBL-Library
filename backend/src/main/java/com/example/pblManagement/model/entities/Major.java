package com.example.pblManagement.model.entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "majors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Major {
    @Id
    @Column(length = 20)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    // Many majors belong to one department
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // One major has many students
    @Builder.Default
    @OneToMany(mappedBy = "major")
    private List<Student> students = new ArrayList<>();

    // 1 class have many majors, and many majors have one class
    @Builder.Default
    @ManyToMany(mappedBy = "majors")
    private List<PblClass> pblClasses = new ArrayList<>();

    // On removal constraints
    @PreRemove
    private void preRemove() {
        if (students != null && !students.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete major with " + students.size() + " student(s)"
            );
        }
    }
}
