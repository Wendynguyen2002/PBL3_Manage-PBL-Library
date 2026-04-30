package com.example.pblManagement.model.entities;
import jakarta.persistence.*;
import lombok.*;

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
    private String id; // Ex: 24Nh11

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
    @OneToMany(mappedBy = "pblClass", cascade = CascadeType.ALL)
    private List<PblGroup> groups = new ArrayList<>();

    // 1 PBL class has many projects to choose from
    @OneToMany(mappedBy = "pblClass", cascade = CascadeType.ALL)
    private List<Project> projects = new ArrayList<>();

    // 1 class may have many tasks.
    @OneToMany(mappedBy = "pblClass", cascade = CascadeType.ALL)
    @OrderBy("dueDate ASC")
    private List<ProgressTask> progressTasks = new ArrayList<>();

    // 1 class with 1 universal final report template
    @OneToOne(mappedBy = "pblClass", cascade = CascadeType.ALL)
    private FinalReportTemplate finalReportTemplate;

    // Students enrolled in this class (whether grouped or not)
    @ManyToMany
    @JoinTable(
            name = "class_enrollments",
            joinColumns = @JoinColumn(name = "pbl_class_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> enrolledStudents = new ArrayList<>();

}
