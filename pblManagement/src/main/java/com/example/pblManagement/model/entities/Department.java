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
    @Column(length = 20)
    private String departmentId;

    @Column(nullable = false, length = 100)
    private String departmentName;

    // One department has many majors
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Major> majors = new ArrayList<>();

    // One department has many lecturers in it
    @OneToMany(mappedBy = "department")
    private List<Lecturer> lecturers = new ArrayList<>();
}
