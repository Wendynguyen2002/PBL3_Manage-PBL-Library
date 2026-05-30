package com.example.pblManagement.service.impl;
import com.example.pblManagement.exceptions.DuplicateResourceException;
import com.example.pblManagement.mappers.ProjectMapper;
import com.example.pblManagement.model.dto.project.ProjectRequestDTO;
import com.example.pblManagement.model.dto.project.ProjectResponseDTO;
import com.example.pblManagement.model.dto.project.ProjectSummaryDTO;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.model.entities.PblClass;
import com.example.pblManagement.model.entities.Project;
import com.example.pblManagement.model.entities.enums.ProjectStatus;
import com.example.pblManagement.model.entities.enums.UserRole;
import com.example.pblManagement.repositories.ProjectRepository;
import com.example.pblManagement.service.ProjectService;
import com.example.pblManagement.utils.PblClassAccessValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final PblClassAccessValidator pblClassAccessValidator;

    // Lecturer: Create a new project for groups to choose from
    @Transactional
    @Override
    public ProjectResponseDTO createProject(String pblClassId, ProjectRequestDTO dto, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new AccessDeniedException("Only lecturers can create projects.");
        }

        PblClass pblClass = pblClassAccessValidator.findClassAndValidateAccessAndReturnEntity(pblClassId, account);

        boolean titleExists = projectRepository.existsByPblClassIdAndTitle(pblClassId, dto.getTitle());
        if (titleExists) {
            throw new DuplicateResourceException("A project with this title already exists in this class");
        }

        Project project = projectMapper.toEntity(dto);
        project.setPblClass(pblClass);
        project.setStatus(ProjectStatus.AVAILABLE);

        return projectMapper.toResponseDTO(projectRepository.save(project));
    }

    // All roles: Get all projects in this class / Student: Get all available projects on creating/updating group
    @Transactional(readOnly = true)
    @Override
    public List<ProjectSummaryDTO> getProjectsByPblClass(String pblClassId, Account account) {
        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        List<Project> projects = projectRepository.findByPblClassId(pblClassId);
        return projects.stream()
                .map(projectMapper::toSummaryDTO)
                .toList();
    }

    // All roles: Get the specific details of a project
    @Transactional(readOnly = true)
    @Override
    public ProjectResponseDTO getProjectById(Long projectId, String pblClassId, Account account) {
        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        Project project = validateProject(projectId, pblClassId);

        return projectMapper.toResponseDTO(project);
    }

    // Lecturer: Update a project
    @Transactional
    @Override
    public ProjectResponseDTO updateProject(Long projectId, String pblClassId, ProjectRequestDTO dto, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new AccessDeniedException("Only lecturers can update projects.");
        }

        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        Project project = validateProject(projectId, pblClassId);

        projectMapper.updateProject(project, dto);
        return projectMapper.toResponseDTO(projectRepository.save(project));
    }

    // Lecturer: Delete a project
    @Transactional
    @Override
    public void deleteProject(Long projectId, String pblClassId, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new AccessDeniedException("Only lecturers can delete projects.");
        }

        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        Project project = validateProject(projectId, pblClassId);

        if (project.getStatus() == ProjectStatus.TAKEN) {
            throw new IllegalStateException("Cannot delete a project that has been taken by a group.");
        }

        projectRepository.delete(project);
    }

    // Helper: Validate project belongs to this class
    public Project validateProject(Long projectId, String pblClassId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));

        if (!project.getPblClass().getId().equals(pblClassId)) {
            throw new IllegalStateException("Project does not belong to the specified class");
        }

        return project;
    }
}
