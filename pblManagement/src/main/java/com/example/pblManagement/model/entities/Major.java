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
    @OneToMany(mappedBy = "major")
    private List<Student> students = new ArrayList<>();
}
