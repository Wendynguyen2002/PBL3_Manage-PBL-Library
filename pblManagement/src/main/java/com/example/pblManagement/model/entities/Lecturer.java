package com.example.pblManagement.model.entities;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lecturers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Lecturer extends Account{
    private String specialization;

    private String position;

    private String degree;

    // Many lecturers belong to one department
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Builder.Default
    @OneToMany(mappedBy = "lecturer")
    private List<PblClass> pblClasses = new ArrayList<>();

    @PreRemove
    private void preRemove() {
        if (pblClasses != null && !pblClasses.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete lecturers present in " + pblClasses.size() + " PBL classes. Delete PBL classes first."
            );
        }
    }
}
