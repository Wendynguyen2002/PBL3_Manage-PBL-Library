package com.example.pblManagement.model.entities;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pbl_classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PblClass {
    @Id
    @Column(nullable = false)
    private String id;

    @Column(nullable = false, length = 100)
    private String className; // Ex: PBL3: Do an cong nghe phan mem

    private String semester;

    @Column(nullable = false)
    private Integer maxStudentsPerGroup; // Maximum number of students allowed in a group

    // Many PBL classes can be taught by 1 lecturer
    @ManyToOne
    @JoinColumn(name = "lecturer_id")
    private Lecturer lecturer;

    // 1 PBL class has many groups in it
    @Builder.Default
    @OneToMany(mappedBy = "pblClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PblGroup> groups = new ArrayList<>();

    // 1 PBL class has many projects to choose from
    @Builder.Default
    @OneToMany(mappedBy = "pblClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects = new ArrayList<>();

    // 1 class may have many tasks.
    @Builder.Default
    @OneToMany(mappedBy = "pblClass", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dueDate ASC")
    private List<ProgressTask> progressTasks = new ArrayList<>();

    // Enrolled students in many classes
    @Builder.Default
    @OneToMany(mappedBy = "pblClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "pbl_class_majors",
            joinColumns = @JoinColumn(name = "pbl_class_id"),
            inverseJoinColumns = @JoinColumn(name = "major_id")
    )
    private List<Major> majors = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "pblClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FinalReport> finalReports = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime finalReportDeadline;  // When final reports are due

}
