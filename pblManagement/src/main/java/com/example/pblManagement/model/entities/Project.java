package com.example.pblManagement.model.entities;
import com.example.pblManagement.model.entities.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// List of projects that Lecturers create for groups to select from
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Many projects to select from in one PBL class
    @ManyToOne
    @JoinColumn(name = "pbl_class_id", nullable = false)
    private PblClass pblClass;

    // 1 project can only be chosen by 1 group
    @OneToOne(mappedBy = "project")
    private PblGroup assignedGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status;

    public boolean isAssigned() {
        return this.assignedGroup != null;
    }

    // Sync status when group assigned
    public void assignGroup(PblGroup group) {
        this.assignedGroup = group;
        this.status = ProjectStatus.TAKEN;
        group.setProject(this);
    }
}
