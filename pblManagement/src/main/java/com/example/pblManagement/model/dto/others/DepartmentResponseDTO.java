package com.example.pblManagement.model.dto.others;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponseDTO {
    private String id;

    private String name;

    @Builder.Default
    private List<String> majorNames = new ArrayList<>();

    @Builder.Default
    private List<String> lecturerNames  = new ArrayList<>();
}
