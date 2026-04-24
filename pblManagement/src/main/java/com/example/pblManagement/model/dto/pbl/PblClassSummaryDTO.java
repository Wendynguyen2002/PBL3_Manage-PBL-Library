package com.example.pblManagement.model.dto.pbl;

import lombok.*;

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
}
