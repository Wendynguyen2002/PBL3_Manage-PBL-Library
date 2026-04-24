package com.example.pblManagement.model.entities;

import com.example.pblManagement.model.entities.enums.TaskSubmissionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "final_report_submissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"template_id", "group_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Each group's final submission (group's view)
public class FinalReportSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200)
    private String projectName;

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    private LocalDateTime lastModifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskSubmissionStatus status; // NOT_SUBMITTED, SUBMITTED, REVIEWED

    // Reference data (denormalized for historical record)
    @Column(nullable = false, length = 100)
    private String classNameSnapshot;

    @Column(nullable = false, length = 100)
    private String lecturerNameSnapshot;

    @Column(nullable = false, length = 100)
    private String projectTitleSnapshot;

    @Column(columnDefinition = "TEXT")
    private String groupMembersSnapshot; // JSON or comma-separated names

    // Relationships
    // Many report submissions for one report template
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private FinalReportTemplate template;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private PblGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by", nullable = false)
    private Student submittedBy;

    // Attachments (final report file + additional URLs)
    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FinalReportAttachment> attachments = new ArrayList<>();

    // Responses to custom fields
    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FinalReportFieldResponse> fieldResponses = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = TaskSubmissionStatus.SUBMITTED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedAt = LocalDateTime.now();
    }

    // Helper: Populate snapshot data
    public void populateSnapshots() {
        PblClass pblClass = template.getPblClass();
        this.classNameSnapshot = pblClass.getClassName();
        this.lecturerNameSnapshot = pblClass.getLecturer().getFullName();

        // Create JSON-like snapshot of group members
        List<String> memberNames = group.getMemberships().stream()
                .map(m -> m.getStudent().getFullName() + " (" + m.getStudent().getId() + ")")
                .collect(Collectors.toList());
        this.groupMembersSnapshot = String.join("; ", memberNames);

        if (group.getProject() != null) {
            this.projectTitleSnapshot = group.getProject().getTitle();
        }
    }

    // Helper: Add field response
    public void addFieldResponse(FinalReportField field, String responseValue) {
        if (fieldResponses == null) {
            fieldResponses = new ArrayList<>();
        }
        FinalReportFieldResponse response = FinalReportFieldResponse.builder()
                .submission(this)
                .field(field)
                .responseValue(responseValue)
                .build();
        fieldResponses.add(response);
    }
}
