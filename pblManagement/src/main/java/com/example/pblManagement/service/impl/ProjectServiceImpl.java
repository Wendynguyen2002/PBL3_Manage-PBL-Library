package com.example.pblManagement.service.impl;
import com.example.pblManagement.mappers.ProjectMapper;
import com.example.pblManagement.model.dto.project.ProjectRequestDTO;
import com.example.pblManagement.model.dto.project.ProjectResponseDTO;
import com.example.pblManagement.model.dto.project.ProjectSummaryDTO;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.model.entities.PblClass;
import com.example.pblManagement.model.entities.Project;
import com.example.pblManagement.model.entities.enums.ProjectStatus;
import com.example.pblManagement.model.entities.enums.UserRole;
import com.example.pblManagement.repositories.PblClassRepository;
import com.example.pblManagement.repositories.ProjectRepository;
import com.example.pblManagement.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final PblClassRepository pblClassRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectResponseDTO createProject(String pblClassId, ProjectRequestDTO dto, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new IllegalStateException("Only lecturers can create projects.");
        }

        PblClass pblClass = pblClassRepository.findById(pblClassId)
                .orElseThrow(() -> new IllegalStateException("PBL class not found."));

        if (pblClass.getLecturer() == null || !pblClass.getLecturer().getId().equals(account.getId())) {
            throw new IllegalStateException("You can only create projects for your own classes");
        }

        Project project = projectMapper.toEntity(dto);
        project.setPblClass(pblClass);
        project.setStatus(ProjectStatus.AVAILABLE);

        return projectMapper.toResponseDTO(projectRepository.save(project));
    }

    @Override
    public List<ProjectSummaryDTO> getProjectsByPblClass(String pblClassId, Account account) {
        validatePblClassAccess(pblClassId, account);

        List<Project> projects = projectRepository.findByPblClassId(pblClassId);
        return projects.stream()
                .map(projectMapper::toSummaryDTO)
                .toList();
    }

    @Override
    public ProjectResponseDTO getProjectById(Long projectId, String pblClassId, Account account) {
        validatePblClassAccess(pblClassId, account);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));

        if (!project.getPblClass().getId().equals(pblClassId)) {
            throw new IllegalStateException("Project does not belong to the specified class");
        }

        return projectMapper.toResponseDTO(project);
    }

    @Override
    public ProjectResponseDTO updateProject(Long projectId, String pblClassId, ProjectRequestDTO dto, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new IllegalStateException("Only lecturers can update projects.");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));

        if (!project.getPblClass().getId().equals(pblClassId)) {
            throw new IllegalStateException("Project does not belong to the specified class");
        }

        PblClass pblClass = project.getPblClass();
        if (pblClass.getLecturer() == null || !pblClass.getLecturer().getId().equals(account.getId())) {
            throw new IllegalStateException("You can only update projects for your own classes");
        }

        projectMapper.updateProject(project, dto);
        return projectMapper.toResponseDTO(projectRepository.save(project));
    }

    @Override
    public void deleteProject(Long projectId, String pblClassId, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new IllegalStateException("Only lecturers can delete projects.");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));

        if(!project.getPblClass().getId().equals(pblClassId)) {
            throw new IllegalStateException("Project does not belong to the specified class");
        }

        PblClass pblClass = project.getPblClass();
        if (pblClass.getLecturer() == null || !pblClass.getLecturer().getId().equals(account.getId())) {
            throw new IllegalStateException("You can only delete projects for your own classes");
        }

        if (project.getStatus() == ProjectStatus.TAKEN) {
            throw new ValidationException("Cannot delete a project that has been taken by a group");
        }

        projectRepository.delete(project);
    }

    @Override
    public List<ProjectSummaryDTO> getAvailableProjects(String pblClassId, Account account) {
        validatePblClassAccess(pblClassId, account);

        List<Project> availableProjects = projectRepository.findAvailableProjectsByClassId(pblClassId);
        return availableProjects.stream()
                .map(projectMapper::toSummaryDTO)
                .toList();
    }

    // Helper method to validate class access
    private void validatePblClassAccess(String pblClassId, Account currentUser) {
        PblClass pblClass = pblClassRepository.findById(pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("PBL class not found with ID: " + pblClassId));

        switch (currentUser.getRole()) {
            case ADMIN -> {
                // Admin can access any class
            }
            case LECTURER -> {
                if (pblClass.getLecturer() != null && pblClass.getLecturer().getId().equals(currentUser.getId())) {
                    return;
                }
                throw new IllegalStateException("You don't have access to this class");
            }
            case STUDENT -> {
                if (pblClassRepository.isStudentEnrolledInClass(pblClassId, currentUser.getId())) {
                    return;
                }
                throw new IllegalStateException("You are not enrolled in this class");
            }
            default -> throw new IllegalStateException("Invalid user role");
        }
    }
}
