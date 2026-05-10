package com.example.pblManagement.model.dto.pbl;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PblClassSummaryDTO {
    private String id;
    private String className;
    private String semester;
    private String lecturerName;
    private List<String> majorNames;
    private LocalDateTime finalReportDeadline;
    private boolean isFinalReportLocked;
}
