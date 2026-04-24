package com.example.pblManagement.model.entities;

import com.example.pblManagement.model.entities.enums.LinkType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "submission_links")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LinkType linkType;

    // Optional description for the link
    @Column(length = 200)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private TaskSubmission submission;
}
