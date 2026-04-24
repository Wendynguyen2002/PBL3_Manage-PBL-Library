package com.example.pblManagement.model.entities;

import com.example.pblManagement.model.entities.enums.AttachmentType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "final_report_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Types of attachment of final report
public class FinalReportAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttachmentType attachmentType;

    @Column(length = 200)
    private String description;

    // There can be many attachments in 1 submission
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private FinalReportSubmission submission;
}
