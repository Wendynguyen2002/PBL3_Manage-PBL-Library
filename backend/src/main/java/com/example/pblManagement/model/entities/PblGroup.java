package com.example.pblManagement.model.entities;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pbl_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PblGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String groupName;

    // Many groups in one class
    @ManyToOne
    @JoinColumn(name = "pbl_class_id")
    private PblClass pblClass;

    // 1 group can only choose one project
    @OneToOne
    @JoinColumn(name = "project_id", unique = true)
    private Project project;

    // 1 group has many enrollments
    @Builder.Default
    @OneToMany(mappedBy = "pblGroup")
    private List<Enrollment> enrollments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "group")
    private List<TaskSubmission> taskSubmission = new ArrayList<>();

    public int getCurrentMemberCount() {
        return enrollments != null ? enrollments.size() : 0;
    }

    public boolean isFull() {
        if (pblClass == null) return true;
        Integer max = pblClass.getMaxStudentsPerGroup();
        return max != null && getCurrentMemberCount() >= max;
    }
}
