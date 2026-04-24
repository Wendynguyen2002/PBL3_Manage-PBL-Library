package com.example.pblManagement.model.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "progress_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Progress tasks will be created by lecturer to track project progresses of all groups
// Viewed only by lecturer
public class ProgressTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Order/sequence of tasks within a class
    private Integer taskOrder;

    // Many tasks belong to one PBL class
    @ManyToOne
    @JoinColumn(name = "pbl_class_id", nullable = false)
    private PblClass pblClass;

    // Many tasks created by this lecturer
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private Lecturer createdBy;

    // One task will have many submissions from many corresponding groups
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<TaskSubmission> submissions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Helper: Check if a group has submitted
    public boolean hasGroupSubmitted(PblGroup group) {
        return submissions != null && submissions.stream()
                .anyMatch(sub -> sub.getGroup().equals(group));
    }

    // Helper: Get submission for a specific group
    public Optional<TaskSubmission> getSubmissionForGroup(PblGroup group) {
        return submissions != null ? submissions.stream()
                                     .filter(sub -> sub.getGroup().equals(group))
                                     .findFirst() : Optional.empty();
    }
}
