package com.example.pblManagement.model.entities;

import com.example.pblManagement.model.entities.enums.TaskSubmissionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_submissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"task_id", "group_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Represents group's submission for a specific task in a PBL course
// Viewed only by students
public class TaskSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String briefDescription;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    // Track if submitted late
    @Column(nullable = false)
    private Boolean isLate;

    // Status for lecturer review
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskSubmissionStatus status;

    // Relationship to task
    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private ProgressTask task;

    // Which group submitted
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private PblGroup group;

    // Links submitted by students
    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubmissionLink> links = new ArrayList<>();

    // Who submitted (the group leader or any member)
    @ManyToOne
    @JoinColumn(name = "submitted_by", nullable = false)
    private Student submittedBy;

    // Optional: Last modified by
    @ManyToOne
    @JoinColumn(name = "last_modified_by")
    private Student lastModifiedBy;

    private LocalDateTime lastModifiedAt;

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
        this.isLate = this.submittedAt.isAfter(task.getDueDate());
        if (this.status == null) {
            this.status = TaskSubmissionStatus.SUBMITTED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedAt = LocalDateTime.now();
    }
}
