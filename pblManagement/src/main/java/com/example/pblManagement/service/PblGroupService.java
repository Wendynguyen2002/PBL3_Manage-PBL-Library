package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.pbl.PblGroupSummaryDTO;
import com.example.pblManagement.model.entities.Account;

import java.util.List;

public interface PblGroupService {
    List<PblGroupSummaryDTO> getGroupsByClass(String pblClassId, Account account);

    PblGroupSummaryDTO createGroup(String pblClassId, Long projectId, Account account);

    void updateGroupProject(Long groupId, Long projectId, Account account);

    void disbandGroup(Long groupId, Account account);

    void joinGroup(Long groupId, String pblClassId, Account account);

    void removeStudentFromGroup(Long groupId, String studentId, String pblClassId, Account account);

    void deleteGroup(Long groupId, String pblClassId, Account account);
}
