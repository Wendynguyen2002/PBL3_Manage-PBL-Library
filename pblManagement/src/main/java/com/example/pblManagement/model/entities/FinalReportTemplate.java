package com.example.pblManagement.model.entities;

import com.example.pblManagement.model.entities.enums.FinalReportFieldType;
import com.example.pblManagement.model.entities.enums.FinalReportStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "final_report_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// A customizable template
public class FinalReportTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FinalReportStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime activatedAt;

    // One template per PBL class
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pbl_class_id", unique = true, nullable = false)
    private PblClass pblClass;

    // Custom fields defined by lecturer
    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fieldOrder ASC")
    private List<FinalReportField> customFields = new ArrayList<>();

    // All group submissions
    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL)
    private List<FinalReportSubmission> submissions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = FinalReportStatus.UNAVAILABLE;
        }
    }

    // Helper: Check if submissions are open
    public boolean isOpenForSubmission() {
        return this.status == FinalReportStatus.AVAILABLE;
    }

    // Helper: Activate template
    public void activate() {
        if (this.status == FinalReportStatus.CLOSED) {
            throw new IllegalStateException("Cannot activate a closed template");
        }
        this.status = FinalReportStatus.AVAILABLE;
        this.activatedAt = LocalDateTime.now();
    }

    // Helper: Add custom field
    public void addField(String label, FinalReportFieldType finalReportFieldType, boolean required,
                         String options, int fieldOrder) {
        if (customFields == null) {
            customFields = new ArrayList<>();
        }
        FinalReportField field = FinalReportField.builder()
                .template(this)
                .fieldLabel(label)
                .finalReportFieldType(finalReportFieldType)
                .required(required)
                .options(options)
                .fieldOrder(fieldOrder)
                .build();
        customFields.add(field);
    }
}
