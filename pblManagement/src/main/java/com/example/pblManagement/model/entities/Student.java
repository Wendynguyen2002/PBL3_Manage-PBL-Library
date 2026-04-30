package com.example.pblManagement.model.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    // One student might be able to be in many groups across many PBL classes
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMembership> groupMemberships = new ArrayList<>();

    @ManyToMany(mappedBy = "enrolledStudents")
    private List<PblClass> enrolledClasses = new ArrayList<>();
}
