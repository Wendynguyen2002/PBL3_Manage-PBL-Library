package com.example.pblManagement.model.dto.pbl;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PblClassResponseDTO {
    private String id;
    private String className;
    private String semester;
    private Integer maxStudentsPerGroup;
    private String lecturerName;
}
