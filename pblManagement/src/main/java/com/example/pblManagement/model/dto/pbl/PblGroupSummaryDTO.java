package com.example.pblManagement.model.dto.pbl;

import com.example.pblManagement.model.dto.user.StudentSummaryDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PblGroupSummaryDTO {
    private Long id;

    private String projectTitle; // Can be null if not chosen

    private String groupName;

    @Builder.Default
    private List<StudentSummaryDTO> members = new ArrayList<>();

    private Integer currentMemberCount;

    private boolean isFull;
}
