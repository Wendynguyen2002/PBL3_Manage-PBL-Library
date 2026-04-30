package com.example.pblManagement.model.dto.others;

import com.example.pblManagement.model.dto.user.LecturerSummaryDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponseDTO {
    private String id;

    private String name;

    private List<String> majorName;

    private List<String> lecturerName;
}
