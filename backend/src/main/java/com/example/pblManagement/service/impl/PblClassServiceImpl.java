package com.example.pblManagement.service.impl;
import com.example.pblManagement.exceptions.DuplicateResourceException;
import com.example.pblManagement.mappers.PblClassMapper;
import com.example.pblManagement.mappers.StudentMapper;
import com.example.pblManagement.model.dto.pbl.PblClassRequestDTO;
import com.example.pblManagement.model.dto.pbl.PblClassResponseDTO;
import com.example.pblManagement.model.dto.pbl.PblClassSummaryDTO;
import com.example.pblManagement.model.dto.user.StudentResponseDTO;
import com.example.pblManagement.model.dto.user.StudentSummaryDTO;
import com.example.pblManagement.model.entities.*;
import com.example.pblManagement.model.entities.enums.StudentGroupStatus;
import com.example.pblManagement.model.entities.enums.UserRole;
import com.example.pblManagement.repositories.*;
import com.example.pblManagement.service.NotificationService;
import com.example.pblManagement.service.PblClassService;
import com.example.pblManagement.utils.PblClassAccessValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PblClassServiceImpl implements PblClassService {
    private final PblClassRepository pblClassRepository;
    private final PblClassMapper pblClassMapper;
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final MajorRepository majorRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationService notificationService;
    private final PblGroupRepository pblGroupRepository;
    private final PblClassAccessValidator pblClassAccessValidator;

    // Lecturer: Create PBL class
    @Transactional
    @Override
    public PblClassResponseDTO createPblClass(PblClassRequestDTO dto, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new AccessDeniedException("Only lecturers can create PBL class");
        }

        // Check if id already exists
        if (pblClassRepository.existsById(dto.getId())) {
            throw new DuplicateResourceException("PBL class ID already exists");
        }

        List<Major> majors = majorRepository.findAllById(dto.getMajorId());
        if (majors.size() != dto.getMajorId().size()) {
            throw new EntityNotFoundException("One or more majors not found");
        }

        Lecturer currentLecturer = (Lecturer) account;

        // Build entity manually — list of majors is hard for MapStruct config
        PblClass pblClass = PblClass.builder()
                .id(dto.getId())
                .className(dto.getClassName())
                .semester(dto.getSemester())
                .maxStudentsPerGroup(dto.getMaxStudentsPerGroup())
                .lecturer(currentLecturer)           // from authenticated account
                .majors(majors)                      // already filtered by dept on frontend
                .groups(new ArrayList<>())
                .projects(new ArrayList<>())
                .progressTasks(new ArrayList<>())
                .enrollments(new ArrayList<>())
                .finalReportDeadline(dto.getFinalReportDeadline())
                .build();

        return pblClassMapper.toResponseDTO(pblClassRepository.save(pblClass));
    }

    // All roles: Each user get to see classes based on their role
    @Transactional(readOnly = true)
    @Override
    public List<PblClassSummaryDTO> getPblClassesForUser(Account account) {
        List<PblClass> PblClasses;
        switch (account.getRole()) {
            case ADMIN -> PblClasses = pblClassRepository.findAll();
            case LECTURER -> PblClasses = pblClassRepository.findByLecturerId(account.getId());
            case STUDENT -> PblClasses = pblClassRepository.findByEnrolledStudentId(account.getId());
            default -> throw new AccessDeniedException("Unexpected role: " + account.getRole());
        }

        return PblClasses.stream()
                .map(pblClassMapper::toSummaryDTO)
                .toList();
    }

    // Lecturer: Get available students for enrollment (filtered by majors) on adding them to this class
    @Transactional(readOnly = true)
    @Override
    public List<StudentSummaryDTO> getAvailableStudentsForClass(String pblClassId, Account account) {
        PblClass pblClass = pblClassAccessValidator.findClassAndValidateAccessAndReturnEntity(pblClassId, account);

        // Get the majors of this class
        List<String> majorIds = pblClass.getMajors().stream()
                .map(Major::getId)
                .toList();

        if (majorIds.isEmpty()) {
            throw new IllegalStateException("No majors assigned to this class. Please assign majors first.");
        }

        // Find students who are in these majors and not already enrolled
        List<Student> availableStudents = pblClassRepository.findAvailableStudentsByMajors(pblClassId, majorIds);

        return availableStudents.stream()
                .map(studentMapper::toSummaryDTO)
                .toList();
    }

    // All roles: Get single class metadata (tab 1)
    @Transactional(readOnly = true)
    @Override
    public PblClassResponseDTO getPblClassById(String pblClassId, Account account) {
        PblClass pblClass = pblClassAccessValidator.findClassAndValidateAccessAndReturnEntity(pblClassId, account);
        return pblClassMapper.toResponseDTO(pblClass);
    }

    // All roles: Get enrolled students for a class (tab 2)
    @Transactional(readOnly = true)
    @Override
    public List<StudentSummaryDTO> getEnrolledStudents(String pblClassId, Account account) {
        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);
        List<Student> students = pblClassRepository.findEnrolledStudentsByPblClassId(pblClassId);
        return students.stream()
                .map(studentMapper::toSummaryDTO)
                .toList();
    }

    // All roles: Get specific student details from a class (tab 2 - modal popup)
    @Transactional(readOnly = true)
    @Override
    public StudentResponseDTO getStudentInClass(String pblClassId, String studentId, Account account) {
        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        // Check if student is enrolled in this class
        boolean isEnrolled = pblClassRepository.isStudentEnrolledInClass(pblClassId, studentId);
        if (!isEnrolled) {
            throw new EntityNotFoundException("Student not enrolled in this class");
        }

        // Get student details
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + studentId));

        return studentMapper.toResponseDTO(student);
    }

    // Lecturer: Add students to their own class
    @Transactional
    @Override
    public void addStudentsToClass(String pblClassId, List<String> studentIds, Account account) {
        PblClass pblClass = pblClassAccessValidator.findClassAndValidateAccessAndReturnEntity(pblClassId, account);

        // Get allowed majors for this class
        List<String> allowedMajorIds = pblClass.getMajors().stream()
                .map(Major::getId)
                .toList();

        if (allowedMajorIds.isEmpty()) {
            throw new IllegalStateException("Cannot add students: No majors assigned to this class");
        }

        List<Enrollment> enrollmentsToSave = new ArrayList<>();

        for (String studentId : studentIds) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new EntityNotFoundException("Student not found: " + studentId));

            // Validate student's major is allowed
            if (!allowedMajorIds.contains(student.getMajor().getId())) {
                throw new IllegalStateException(
                        String.format("Student %s (%s) is not eligible for this class. Allowed majors: %s",
                                student.getFullName(),
                                student.getMajor().getName(),
                                allowedMajorIds)
                );
            }

            // Check if already enrolled
            if (enrollmentRepository.existsByStudentIdAndPblClassId(studentId, pblClassId)) {
                throw new DuplicateResourceException("Student " + studentId + " is already enrolled in this class");
            }

            Enrollment enrollment = Enrollment.builder()
                    .student(student)
                    .pblClass(pblClass)
                    .pblGroup(null) // Not in a group yet
                    .status(StudentGroupStatus.NOT_IN_GROUP)
                    .build();

            enrollmentsToSave.add(enrollment);
        }
        enrollmentRepository.saveAll(enrollmentsToSave);

        // Create notifications for each added student
        for (Enrollment enrollment : enrollmentsToSave) {
            Student student = enrollment.getStudent();
            notificationService.createNotification(
                    student.getId(),
                    UserRole.STUDENT,  // Since only students get enrolled
                    "Added to PBL Class",
                    String.format("You have been added to PBL class '%s' (%s)",
                            pblClass.getClassName(), pblClass.getId()),
                    "CLASS_ENROLLMENT",
                    pblClass.getId()
            );
        }
    }

    // Lecturer: Remove students from their own class
    @Transactional
    @Override
    public void removeStudentFromClass(String pblClassId, String studentId, Account account) {
        PblClass pblClass = pblClassAccessValidator.findClassAndValidateAccessAndReturnEntity(pblClassId, account);

        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndPblClassId(studentId, pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("Student not enrolled in this class"));

        // Check if student is in a group
        if (enrollment.getPblGroup() != null) {
            throw new IllegalStateException("Cannot remove student from class because they are currently in a group. Remove them from the group first.");
        }

        notificationService.createNotification(
                enrollment.getStudent().getId(),
                UserRole.STUDENT,
                "Removed from PBL Class",
                String.format("You have been removed from PBL class '%s' (%s)",
                        pblClass.getClassName(), pblClass.getId()),
                "CLASS_REMOVAL",
                pblClassId
        );

        enrollmentRepository.delete(enrollment);
    }

    // Lecturer: Update their own PBL class
    @Transactional
    @Override
    public PblClassResponseDTO updatePblClass(String pblClassId, PblClassRequestDTO dto, Account account) {
        PblClass existingClass = pblClassAccessValidator.findClassAndValidateAccessAndReturnEntity(pblClassId, account);

        // Prevent changing majors after creation
        if (dto.getMajorId() != null && !dto.getMajorId().isEmpty()) {
            List<String> existingMajorIds = existingClass.getMajors().stream()
                    .map(Major::getId)
                    .toList();
            List<String> newMajorIds = dto.getMajorId();

            if (!existingMajorIds.equals(newMajorIds)) {
                throw new IllegalStateException("Cannot change class majors after creation. Delete and recreate the class if needed.");
            }
        }

        // Validate group size change if present
        if (dto.getMaxStudentsPerGroup() != null) {
            validateGroupSizeChanges(existingClass, dto);
        }

        // Manual update for allowed fields only
        if (dto.getClassName() != null) {
            existingClass.setClassName(dto.getClassName());
        }
        if (dto.getSemester() != null) {
            existingClass.setSemester(dto.getSemester());
        }
        if (dto.getMaxStudentsPerGroup() != null) {
            existingClass.setMaxStudentsPerGroup(dto.getMaxStudentsPerGroup());
        }

        return pblClassMapper.toResponseDTO(pblClassRepository.save(existingClass));
    }

    // Lecturer: Delete their own PBL class / Admin: Able to delete any class
    @Transactional
    @Override
    public void deletePblClass(String pblClassId, Account account) {
        PblClass pblClass = pblClassAccessValidator.findClassAndValidateAccessAndReturnEntity(pblClassId, account);
        pblClassRepository.delete(pblClass);
    }

    // Helper method to validate group size changes
    private void validateGroupSizeChanges(PblClass existingClass, PblClassRequestDTO requestDTO) {
        // Use repository query to check if any group exceeds new limit
        if (requestDTO.getMaxStudentsPerGroup() != null) {
            // Fetch only group IDs and their member counts efficiently
            List<Object[]> groupSizes = pblGroupRepository.findGroupIdsAndMemberCounts(existingClass.getId());

            for (Object[] groupData : groupSizes) {
                Long memberCount = (Long) groupData[1];
                String groupName = (String) groupData[2];

                if (memberCount > requestDTO.getMaxStudentsPerGroup()) {
                    throw new IllegalStateException(
                            String.format("Cannot decrease maximum group size to %d because group '%s' currently has %d members",
                                    requestDTO.getMaxStudentsPerGroup(), groupName, memberCount)
                    );
                }
            }
        }
    }

}
