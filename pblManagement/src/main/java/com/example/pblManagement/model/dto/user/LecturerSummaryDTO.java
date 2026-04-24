package com.example.pblManagement.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LecturerSummaryDTO extends AccountSummaryDTO{
    private String degree;
    private String departmentName;
}
