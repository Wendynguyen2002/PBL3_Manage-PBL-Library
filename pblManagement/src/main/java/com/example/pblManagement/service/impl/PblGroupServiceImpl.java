package com.example.pblManagement.service.impl;
import com.example.pblManagement.mappers.PblGroupMapper;
import com.example.pblManagement.model.dto.pbl.PblGroupSummaryDTO;
import com.example.pblManagement.model.entities.*;
import com.example.pblManagement.model.entities.enums.ProjectStatus;
import com.example.pblManagement.model.entities.enums.StudentGroupStatus;
import com.example.pblManagement.model.entities.enums.UserRole;
import com.example.pblManagement.repositories.*;
import com.example.pblManagement.service.PblGroupService;
import com.example.pblManagement.utils.PblClassAccessValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PblGroupServiceImpl implements PblGroupService {
    private final PblGroupRepository pblGroupRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ProjectRepository projectRepository;
    private final PblGroupMapper pblGroupMapper;
    private final PblClassAccessValidator pblClassAccessValidator;

    // ==================== HELPER METHODS ====================

    // Helper: Generate random group name (A1, B2, C3, etc.)
    private String generateGroupName(PblClass pblClass) {
        long groupCount = pblGroupRepository.findByPblClassIdOrderByGroupName(pblClass.getId()).size();
        char letter = (char) ('A' + (groupCount / 26) % 26);
        int number = Math.toIntExact((groupCount % 26) + 1);
        return String.format("Group %c%d", letter, number);
    }

    // Helper: Validate group exists in class
    private PblGroup findGroupInClass(Long groupId, String pblClassId) {
        return pblGroupRepository.findByIdAndPblClassId(groupId, pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found in this class"));
    }

    // Helper: Get student's enrollment in a class
    private Enrollment getStudentEnrollment(String studentId, String pblClassId) {
        return enrollmentRepository.findByStudentIdAndPblClassId(studentId, pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("Student not enrolled in this class"));
    }

    // Helper: Validate student is not already in any group in this class
    private void validateStudentNotInAnyGroup(String studentId, String pblClassId) {
        if (pblGroupRepository.isStudentInAnyGroup(studentId, pblClassId)) {
            throw new IllegalStateException("You are already in a group in this class");
        }
    }

    // Helper: Clean up project assignment when a group is deleted or changes project
    private void freeProject(Project project) {
        if (project != null) {
            project.setStatus(ProjectStatus.AVAILABLE);
            project.setAssignedGroup(null);
            projectRepository.save(project);
        }
    }

    // Validate and assign project to a group on creating/updating
    private Project validateAndReserveProject(Long projectId, String pblClassId, PblGroup pblGroup) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));

        // Verify project belongs to this class
        if (!project.getPblClass().getId().equals(pblClassId)) {
            throw new IllegalStateException("Project does not belong to this class");
        }

        // Verify project is available
        if (project.getStatus() != ProjectStatus.AVAILABLE) {
            throw new IllegalStateException("Project is already taken");
        }

        // Save
        project.setStatus(ProjectStatus.TAKEN);
        project.setAssignedGroup(pblGroup);
        return projectRepository.save(project);
    }

    // ==================== PUBLIC METHODS ====================

    // All roles: Get groups of a PBL class
    @Transactional(readOnly = true)
    @Override
    public List<PblGroupSummaryDTO> getGroupsByClass(String pblClassId, Account account) {
        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        return pblGroupRepository.findByPblClassIdOrderByGroupName(pblClassId).stream()
                .map(pblGroupMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }

    // Student: Create new group
    @Transactional
    @Override
    public PblGroupSummaryDTO createGroup(String pblClassId, Long projectId, Account account) {
        // Only students can create groups
        if (account.getRole() != UserRole.STUDENT) {
            throw new AccessDeniedException("Only students can create groups");
        }

        PblClass pblClass = pblClassAccessValidator.findClassAndValidateAccessAndReturnEntity(pblClassId, account);

        validateStudentNotInAnyGroup(account.getId(), pblClassId);

        // Create group
        PblGroup group = PblGroup.builder()
                .groupName(generateGroupName(pblClass))
                .pblClass(pblClass)
                .enrollments(new ArrayList<>())
                .build();

        pblGroupRepository.save(group);

        // Validate project if provided on creating a group
        if (projectId != null) {
            Project project = validateAndReserveProject(projectId, pblClassId, group);
            group.setProject(project);
            pblGroupRepository.save(group);
        }

        // Get student's enrollment in this class
        Enrollment enrollment = getStudentEnrollment(account.getId(), pblClassId);

        // Update enrollment with group
        enrollment.setPblGroup(group);
        enrollment.setStatus(StudentGroupStatus.IN_GROUP);
        enrollmentRepository.save(enrollment);

        return pblGroupMapper.toSummaryDTO(group);
    }

    // Student: Update their own group's project
    @Transactional
    @Override
    public void updateGroupProject(Long groupId, Long projectId, Account account) {
        // Only students can update their own group's project
        if (account.getRole() != UserRole.STUDENT) {
            throw new AccessDeniedException("Only students can update group project");
        }

        // Find the group
        PblGroup group = pblGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found: " + groupId));

        boolean isMember = group.getEnrollments().stream()
                .anyMatch(e -> e.getStudent().getId().equals(account.getId()));

        if (!isMember) {
            throw new AccessDeniedException("You are not a member of this group");
        }

        String pblClassId = group.getPblClass().getId();

        // Case 1: Remove project
        if (projectId == null) {
            freeProject(group.getProject());
            group.setProject(null);
            pblGroupRepository.save(group);
            return;
        }

        // Case 2: Change to new project
        Project newProject = validateAndReserveProject(projectId, pblClassId, group);

        // Free old project if exists
        freeProject(group.getProject());

        // Assign new project
        group.setProject(newProject);
        pblGroupRepository.save(group);
    }

    // Student: Disband their own group if they are the only one member
    @Transactional
    @Override
    public void disbandGroup(Long groupId, Account account) {
        // Only students can disband their own group
        if (account.getRole() != UserRole.STUDENT) {
            throw new AccessDeniedException("Only students can disband groups");
        }

        PblGroup group = pblGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found: " + groupId));

        // Check student is a member of this group
        Enrollment creatorEnrollment = group.getEnrollments().stream()
                .filter(e -> e.getStudent().getId().equals(account.getId()))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("You are not a member of this group"));

        // Check if they are the only member
        if (group.getEnrollments().size() > 1) {
            throw new AccessDeniedException("Cannot disband group with multiple members. Only possible when you're the only member.");
        }

        // Free up project if assigned
        freeProject(group.getProject());

        // Update creator's enrollment status
        creatorEnrollment.setPblGroup(null);
        creatorEnrollment.setStatus(StudentGroupStatus.NOT_IN_GROUP);
        enrollmentRepository.save(creatorEnrollment);

        // Delete the group
        pblGroupRepository.delete(group);
    }

    // Student: Join a group that is not full yet
    @Transactional
    @Override
    public void joinGroup(Long groupId, String pblClassId, Account account) {
        // Only students can join groups
        if (account.getRole() != UserRole.STUDENT) {
            throw new AccessDeniedException("Only students can join groups");
        }

        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        // Check student is not already in ANY group in this class
        validateStudentNotInAnyGroup(account.getId(), pblClassId);

        // Find the group
        PblGroup group = findGroupInClass(groupId, pblClassId);

        // Check group is not full
        if (group.isFull()) {
            throw new AccessDeniedException("Group is already full. Cannot join.");
        }

        // Get student's enrollment
        Enrollment enrollment = getStudentEnrollment(account.getId(), pblClassId);

        // Join the group
        enrollment.setPblGroup(group);
        enrollment.setStatus(StudentGroupStatus.IN_GROUP);
        group.getEnrollments().add(enrollment);
        enrollmentRepository.save(enrollment);
    }

    // Lecturer: Remove a student from a group
    @Transactional
    @Override
    public void removeStudentFromGroup(Long groupId, String studentId, String pblClassId, Account account) {
        // Only lecturers can remove students from groups
        if (account.getRole() != UserRole.LECTURER) {
            throw new AccessDeniedException("Only lecturers can remove students from groups");
        }

        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        // Find the group
        PblGroup group = pblGroupRepository.findByIdWithEnrollments(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found: " + groupId));

        if (!group.getPblClass().getId().equals(pblClassId)) {
            throw new IllegalStateException("Group does not belong to this class");
        }

        // Get student's enrollment
        Enrollment enrollment = getStudentEnrollment(studentId, pblClassId);

        // Check student is actually in this group
        if (enrollment.getPblGroup() == null || !enrollment.getPblGroup().getId().equals(groupId)) {
            throw new IllegalStateException("Student is not in this group");
        }

        // Remove student from group
        enrollment.setPblGroup(null);
        enrollment.setStatus(StudentGroupStatus.NOT_IN_GROUP);
        enrollmentRepository.save(enrollment);

        // Check if group is now empty - if yes, delete it and free the project
        if (group.getEnrollments().isEmpty()) {
            freeProject(group.getProject());
            pblGroupRepository.delete(group);
        }
    }

    // Lecturer: Forcefully delete a group
    @Transactional
    @Override
    public void deleteGroup(Long groupId, String pblClassId, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new AccessDeniedException("Only lecturers can delete groups");
        }

        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        // Find the group
        PblGroup group = pblGroupRepository.findByIdWithEnrollments(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found: " + groupId));

        // Free up project if assigned
        freeProject(group.getProject());

        // Remove all students from this group by updating their enrollments
        for (Enrollment enrollment : group.getEnrollments()) {
            enrollment.setPblGroup(null);
            enrollment.setStatus(StudentGroupStatus.NOT_IN_GROUP);
            enrollmentRepository.save(enrollment);
        }

        // Delete the group
        pblGroupRepository.delete(group);
    }

}
