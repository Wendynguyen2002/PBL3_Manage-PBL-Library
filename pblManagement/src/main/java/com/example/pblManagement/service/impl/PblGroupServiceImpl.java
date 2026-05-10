package com.example.pblManagement.service.impl;
import com.example.pblManagement.mappers.PblGroupMapper;
import com.example.pblManagement.model.dto.pbl.PblGroupSummaryDTO;
import com.example.pblManagement.model.entities.*;
import com.example.pblManagement.model.entities.enums.ProjectStatus;
import com.example.pblManagement.model.entities.enums.StudentGroupStatus;
import com.example.pblManagement.model.entities.enums.UserRole;
import com.example.pblManagement.repositories.*;
import com.example.pblManagement.service.PblGroupService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PblGroupServiceImpl implements PblGroupService {
    private final PblGroupRepository pblGroupRepository;
    private final PblClassRepository pblClassRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ProjectRepository projectRepository;
    private final StudentRepository studentRepository;
    private final PblGroupMapper pblGroupMapper;

    // Helper: Generate random group name (A1, B2, C3, etc.)
    private String generateGroupName(PblClass pblClass) {
        long groupCount = pblGroupRepository.findByPblClassIdOrderByGroupName(pblClass.getId()).size();
        char letter = (char) ('A' + (groupCount / 26) % 26);
        int number = Math.toIntExact((groupCount % 26) + 1);
        return String.format("Group %c%d", letter, number);
    }

    // Helper: Validate access to class
    private PblClass findClassAndValidateAccess(String classId, Account account) {
        PblClass pblClass = pblClassRepository.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("PBL class not found: " + classId));

        return switch (account.getRole()) {
            case ADMIN -> pblClass;
            case LECTURER -> {
                if (pblClass.getLecturer().getId().equals(account.getId())) {
                    yield pblClass;
                }
                throw new IllegalStateException("You don't have access to this class");
            }
            case STUDENT -> {
                if (pblClassRepository.isStudentEnrolledInClass(classId, account.getId())) {
                    yield pblClass;
                }
                throw new IllegalStateException("You are not enrolled in this class");
            }
        };
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

    // Get groups of a PBL class
    @Override
    public List<PblGroupSummaryDTO> getGroupsByClass(String pblClassId, Account account) {
        findClassAndValidateAccess(pblClassId, account);

        List<PblGroup> groups = pblGroupRepository.findByPblClassIdOrderByGroupName(pblClassId);
        return groups.stream()
                .map(pblGroupMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }

    // Student: Create new group
    @Override
    public PblGroupSummaryDTO createGroup(String pblClassId, Long projectId, Account account) {
        // Only students can create groups
        if (account.getRole() != UserRole.STUDENT) {
            throw new IllegalStateException("Only students can create PBL groups");
        }

        // Check student is enrolled in this class
        PblClass pblClass = findClassAndValidateAccess(pblClassId, account);

        // Check student is not already in ANY group in this class
        if (pblGroupRepository.isStudentInAnyGroup(account.getId(), pblClassId)) {
            throw new ValidationException("You are already in a group in this class");
        }

        // Validate project if provided
        Project project = null;
        if (projectId != null) {
            project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));

            // Verify project belongs to this class
            if (!project.getPblClass().getId().equals(pblClassId)) {
                throw new ValidationException("Project does not belong to this class");
            }

            // Verify project is available
            if (project.getStatus() != ProjectStatus.AVAILABLE) {
                throw new ValidationException("Project is already taken");
            }

            // Mark project as taken
            project.setStatus(ProjectStatus.TAKEN);
        }

        // Generate random group name
        String groupName = generateGroupName(pblClass);

        // Create group
        PblGroup group = PblGroup.builder()
                .groupName(groupName)
                .pblClass(pblClass)
                .project(project)
                .enrollments(new ArrayList<>())
                .build();

        pblGroupRepository.save(group);

        // Get student entity
        Student student = studentRepository.findById(account.getId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        // Get student's enrollment in this class
        Enrollment enrollment = getStudentEnrollment(account.getId(), pblClassId);

        // Update enrollment with group
        enrollment.setPblGroup(group);
        enrollment.setStatus(StudentGroupStatus.IN_GROUP);
        enrollmentRepository.save(enrollment);

        return pblGroupMapper.toSummaryDTO(group);
    }

    @Override
    public void updateGroupProject(Long groupId, Long projectId, Account account) {
        // Only students can update their own group's project
        if (account.getRole() != UserRole.STUDENT) {
            throw new IllegalStateException("Only students can update group project");
        }

        // Find the group
        PblGroup group = pblGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found: " + groupId));

        // Check student is a member of this group
        boolean isMember = group.getEnrollments().stream()
                .anyMatch(e -> e.getStudent().getId().equals(account.getId()));

        if (!isMember) {
            throw new IllegalStateException("You can only update your own group's project");
        }

        String pblClassId = group.getPblClass().getId();

        // Handle removing project (projectId == null)
        if (projectId == null) {
            if (group.getProject() != null) {
                // Free up the old project
                Project oldProject = group.getProject();
                oldProject.setStatus(ProjectStatus.AVAILABLE);
                projectRepository.save(oldProject);
                group.setProject(null);
            }
            pblGroupRepository.save(group);
            return;
        }

        // Handle changing to a new project
        Project newProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));

        // Verify project belongs to this class
        if (!newProject.getPblClass().getId().equals(pblClassId)) {
            throw new ValidationException("Project does not belong to this class");
        }

        // Verify project is available
        if (newProject.getStatus() != ProjectStatus.AVAILABLE) {
            throw new ValidationException("Project is already taken");
        }

        // Free up old project if exists
        if (group.getProject() != null) {
            Project oldProject = group.getProject();
            oldProject.setStatus(ProjectStatus.AVAILABLE);
            projectRepository.save(oldProject);
        }

        // Assign new project
        newProject.setStatus(ProjectStatus.TAKEN);
        group.setProject(newProject);

        pblGroupRepository.save(group);
        projectRepository.save(newProject);
    }

    @Override
    public void disbandGroup(Long groupId, Account account) {
        // Only students can disband their own group
        if (account.getRole() != UserRole.STUDENT) {
            throw new IllegalStateException("Only students can disband groups");
        }

        PblGroup group = pblGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found: " + groupId));

        // Check student is a member of this group
        Enrollment creatorEnrollment = group.getEnrollments().stream()
                .filter(e -> e.getStudent().getId().equals(account.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("You are not a member of this group"));

        // Check they are the only member
        if (group.getEnrollments().size() > 1) {
            throw new ValidationException("Cannot disband group with multiple members. Only possible when you're the only member.");
        }

        // Free up project if assigned
        if (group.getProject() != null) {
            Project project = group.getProject();
            project.setStatus(ProjectStatus.AVAILABLE);
            projectRepository.save(project);
        }

        // Update creator's enrollment status
        creatorEnrollment.setPblGroup(null);
        creatorEnrollment.setStatus(StudentGroupStatus.NOT_IN_GROUP);
        enrollmentRepository.save(creatorEnrollment);

        // Delete the group
        pblGroupRepository.delete(group);
    }

    @Override
    public void joinGroup(Long groupId, String pblClassId, Account account) {
        // Only students can join groups
        if (account.getRole() != UserRole.STUDENT) {
            throw new IllegalStateException("Only students can join groups");
        }

        // Check student is enrolled in this class
        findClassAndValidateAccess(pblClassId, account);

        // Check student is not already in ANY group in this class
        if (pblGroupRepository.isStudentInAnyGroup(account.getId(), pblClassId)) {
            throw new ValidationException("You are already in a group in this class. Cannot join another.");
        }

        // Find the group
        PblGroup group = findGroupInClass(groupId, pblClassId);

        // Check group is not full
        if (group.isFull()) {
            throw new ValidationException("Group is already full. Cannot join.");
        }

        // Get student's enrollment
        Enrollment enrollment = getStudentEnrollment(account.getId(), pblClassId);

        // Join the group
        enrollment.setPblGroup(group);
        enrollment.setStatus(StudentGroupStatus.IN_GROUP);
        enrollmentRepository.save(enrollment);
    }

    @Override
    public void removeStudentFromGroup(Long groupId, String studentId, String pblClassId, Account account) {
        // Only lecturers can remove students from groups
        if (account.getRole() != UserRole.LECTURER && account.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("Only lecturers and admins can remove students from groups");
        }

        // Verify access to class
        findClassAndValidateAccess(pblClassId, account);

        // Find the group
        PblGroup group = findGroupInClass(groupId, pblClassId);

        // Get student's enrollment
        Enrollment enrollment = getStudentEnrollment(studentId, pblClassId);

        // Check student is actually in this group
        if (enrollment.getPblGroup() == null || !enrollment.getPblGroup().getId().equals(groupId)) {
            throw new ValidationException("Student is not in this group");
        }

        // Remove student from group
        enrollment.setPblGroup(null);
        enrollment.setStatus(StudentGroupStatus.NOT_IN_GROUP);
        enrollmentRepository.save(enrollment);
    }

    @Override
    public void deleteGroup(Long groupId, String pblClassId, Account account) {
        // Only lecturers can delete groups
        if (account.getRole() != UserRole.LECTURER && account.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("Only lecturers and admins can delete groups");
        }

        // Verify access to class
        findClassAndValidateAccess(pblClassId, account);

        // Find the group
        PblGroup group = findGroupInClass(groupId, pblClassId);

        // Free up project if assigned
        if (group.getProject() != null) {
            Project project = group.getProject();
            project.setStatus(ProjectStatus.AVAILABLE);
            projectRepository.save(project);
        }

        // Remove all students from this group (update their enrollments)
        for (Enrollment enrollment : group.getEnrollments()) {
            enrollment.setPblGroup(null);
            enrollment.setStatus(StudentGroupStatus.NOT_IN_GROUP);
            enrollmentRepository.save(enrollment);
        }

        // Delete the group
        pblGroupRepository.delete(group);
    }

}
