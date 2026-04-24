package com.example.pblManagement.model.dto.pbl;

import com.example.pblManagement.model.dto.user.StudentSummaryDTO;
import lombok.*;

import java.util.List;

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
    private Integer minStudentsPerGroup;
    private String lecturerName;
    private List<StudentSummaryDTO> students;
}
