package com.example.pblManagement.model.entities;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "final_reports",
        uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "pbl_class_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;  // Optional for students, but good for library

    @Column(columnDefinition = "TEXT")
    private String description;  // Optional, what the report is about

    @Column(nullable = false)
    private String filePath;  // Path or URL to stored file

    @Column(nullable = false)
    private String fileType;  // PDF, DOCX, PPT

    @Column(nullable = false)
    private String originalFileName;  // User's original filename

    @Column(nullable = false)
    private LocalDateTime submittedAt;

    private LocalDateTime lastModifiedAt;

    // Which group submitted this report
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private PblGroup group;

    // Which class (redundant but useful for queries)
    @ManyToOne
    @JoinColumn(name = "pbl_class_id", nullable = false)
    private PblClass pblClass;

    // Who submitted (the student who uploaded)
    @ManyToOne
    @JoinColumn(name = "submitted_by", nullable = false)
    private Student submittedBy;

    // Who last modified (if editing allowed)
    @ManyToOne
    @JoinColumn(name = "last_modified_by")
    private Student lastModifiedBy;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedAt = LocalDateTime.now();
    }

    @Column(nullable = false)
    private boolean isPublic = false;

    private Double averageRating;

    private Integer ratingCount = 0;

    private Integer downloadCount = 0;

    // Helper methods
    public void updateAverageRating(int newRating, int oldRating) {
        if (ratingCount == null) ratingCount = 0;
        double total = (averageRating != null ? averageRating * ratingCount : 0);
        if (oldRating > 0) {
            total -= oldRating;
            ratingCount--;
        }
        total += newRating;
        ratingCount++;
        averageRating = ratingCount > 0 ? total / ratingCount : null;
    }
}
