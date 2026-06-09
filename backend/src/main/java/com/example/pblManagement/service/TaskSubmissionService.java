package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.progress_task.TaskSubmissionRequestDTO;
import com.example.pblManagement.model.dto.progress_task.TaskSubmissionResponseDTO;
import com.example.pblManagement.model.dto.progress_task.TaskSubmissionSummaryDTO;
import com.example.pblManagement.model.entities.Account;

import java.util.List;

public interface TaskSubmissionService {
    TaskSubmissionResponseDTO submitOrUpdateSubmission(Long taskId, String pblClassId,
                                                       TaskSubmissionRequestDTO dto, Account account);

    TaskSubmissionResponseDTO getMyGroupSubmission(Long taskId, String pblClassId, Account account);

    List<TaskSubmissionSummaryDTO> getAllSubmissionsForTask(Long taskId, String pblClassId, Account account);

    TaskSubmissionResponseDTO getSubmissionByGroup(Long taskId, String pblClassId, Long groupId, Account account);
}
