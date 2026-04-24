package com.example.pblManagement.model.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "final_report_field_responses",
        uniqueConstraints = @UniqueConstraint(columnNames = {"submission_id", "field_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Each group's response to each custom field
public class FinalReportFieldResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String responseValue;

    // Many responses in 1 submission
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private FinalReportSubmission submission;

    // Many responses (like multiple checkboxes) in 1 field
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    private FinalReportField field;
}
