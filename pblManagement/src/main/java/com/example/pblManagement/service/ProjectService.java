package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.project.ProjectRequestDTO;
import com.example.pblManagement.model.dto.project.ProjectResponseDTO;
import com.example.pblManagement.model.dto.project.ProjectSummaryDTO;
import com.example.pblManagement.model.entities.Account;

import java.util.List;

public interface ProjectService {

    ProjectResponseDTO createProject(String pblClassId, ProjectRequestDTO dto, Account account);

    List<ProjectSummaryDTO> getProjectsByPblClass(String pblClassId, Account account);

    ProjectResponseDTO getProjectById(Long projectId, String pblClassId, Account account);

    ProjectResponseDTO updateProject(Long projectId, String pblClassId, ProjectRequestDTO dto, Account account);

    void deleteProject(Long projectId, String pblClassId, Account account);

    List<ProjectSummaryDTO> getAvailableProjects(String pblClassId, Account account);
}
