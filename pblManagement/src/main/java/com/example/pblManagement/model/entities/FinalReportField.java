package com.example.pblManagement.model.entities;

import com.example.pblManagement.model.entities.enums.FinalReportFieldType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "final_report_fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Lecturer may add more fields to the final report template, such as "Project Type" (dropdown),
// "Technologies Used" (checkbox), "Challenges Faced" (text), etc.
public class FinalReportField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String fieldLabel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FinalReportFieldType finalReportFieldType;

    @Column(nullable = false)
    private boolean required;

    // For checkbox/radio/dropdown: comma-separated options
    // Example: "Web,Desktop,Mobile" or "Yes,No"
    @Column(length = 500)
    private String options;

    @Column(nullable = false)
    private Integer fieldOrder;

    // May add many fields in 1 template
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private FinalReportTemplate template;

    // Stored responses for this field
    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    private List<FinalReportFieldResponse> responses = new ArrayList<>();

    // Helper: Parse options into list
    public List<String> getOptionsList() {
        if (options == null || options.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(options.split(","));
    }
}
