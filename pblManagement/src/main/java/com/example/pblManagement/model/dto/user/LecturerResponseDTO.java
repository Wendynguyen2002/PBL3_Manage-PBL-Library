package com.example.pblManagement.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class LecturerResponseDTO extends AccountResponseDTO{
    private String id;
    private String specialization;
    private String position;
    private String degree;
    private String departmentName;
    private List<String> pblClassIds;
    private List<String> pblClassNames;
}
