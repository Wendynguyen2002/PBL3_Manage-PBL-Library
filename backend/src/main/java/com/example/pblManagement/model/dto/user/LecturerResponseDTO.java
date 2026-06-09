package com.example.pblManagement.model.dto.user;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class LecturerResponseDTO extends AccountResponseDTO {
    private String specialization;

    private String position;

    private String degree;

    private String departmentName;

    @Builder.Default
    private List<String> pblClassNames =  new ArrayList<>();

    @Builder.Default
    private List<String> pblClassSemesters = new ArrayList<>();
}
