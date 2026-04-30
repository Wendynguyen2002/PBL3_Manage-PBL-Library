package com.example.pblManagement.model.dto.pbl;

import com.example.pblManagement.model.dto.user.StudentSummaryDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PblGroupSummaryDTO {
    private String projectTitle; // Can be null if not chosen
    private String groupName;
    private List<StudentSummaryDTO> members;
    private Integer currentMemberCount;
    private boolean isFull;
}
