package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.progress_task.ProgressTaskRequestDTO;
import com.example.pblManagement.model.dto.progress_task.ProgressTaskResponseDTO;
import com.example.pblManagement.model.dto.progress_task.ProgressTaskSummaryDTO;
import com.example.pblManagement.model.entities.Account;

import java.util.List;

public interface ProgressTaskService {
    ProgressTaskResponseDTO createTask(String pblClassId, ProgressTaskRequestDTO dto, Account account);

    ProgressTaskResponseDTO updateTask(Long taskId, String pblClassId, ProgressTaskRequestDTO dto, Account account);

    void deleteTask(Long taskId, String pblClassId, Account account);

    List<ProgressTaskSummaryDTO> getTasksByClass(String pblClassId, Account account);

    ProgressTaskResponseDTO getTaskById(Long taskId, String pblClassId, Account account);
}
