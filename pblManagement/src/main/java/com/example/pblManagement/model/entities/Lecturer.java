package com.example.pblManagement.model.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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

    @OneToMany
    @JoinColumn(name = "pbl_class_id")
    private List<PblClass> pblClasses;
}
