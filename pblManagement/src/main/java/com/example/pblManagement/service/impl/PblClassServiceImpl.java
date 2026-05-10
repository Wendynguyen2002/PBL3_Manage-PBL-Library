package com.example.pblManagement.service.impl;
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
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PblClassServiceImpl implements PblClassService {
    private final PblClassRepository pblClassRepository;
    private final PblClassMapper pblClassMapper;
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final MajorRepository majorRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationService notificationService;

    // Lecturer: Create PBL class
    @Override
    public PblClassResponseDTO createPblClass(PblClassRequestDTO dto, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new IllegalStateException("Only lecturers can create PBL class");
        }

        // Check if id already exists
        if (pblClassRepository.existsById(dto.getId())) {
            throw new ValidationException("PBL class ID already exists");
        }

        // Validate majors exist
        if (dto.getMajorId() == null || dto.getMajorId().isEmpty()) {
            throw new ValidationException("At least one major must be selected");
        }

        List<Major> majors = majorRepository.findAllById(dto.getMajorId());
        if (majors.size() != dto.getMajorId().size()) {
            throw new EntityNotFoundException("One or more majors not found");
        }

        Lecturer currentLecturer = (Lecturer) account;

        // Build entity manually — IGNORE lecturerId and departmentId from DTO
        PblClass pblClass = PblClass.builder()
                .id(dto.getId())
                .className(dto.getClassName())
                .semester(dto.getSemester())
                .maxStudentsPerGroup(dto.getMaxStudentsPerGroup())
                .lecturer(currentLecturer)           // ← from authenticated account
                .majors(majors)                      // ← already filtered by dept on frontend
                .groups(new ArrayList<>())
                .projects(new ArrayList<>())
                .progressTasks(new ArrayList<>())
                .enrollments(new ArrayList<>())
                .finalReportDeadline(dto.getFinalReportDeadline())
                .isFinalReportLocked(false)
                .build();

        return pblClassMapper.toResponseDTO(pblClassRepository.save(pblClass));
    }

    @Override
    public List<PblClassSummaryDTO> getPblClassesForUser(Account account) {
        List<PblClass> PblClasses;
        switch (account.getRole()) {
            case ADMIN -> PblClasses = pblClassRepository.findAll();
            case LECTURER -> PblClasses = pblClassRepository.findByLecturerId(account.getId());
            case STUDENT -> PblClasses = pblClassRepository.findByEnrolledStudentId(account.getId());
            default -> throw new IllegalStateException("Unexpected role: " + account.getRole());
        }

        return PblClasses.stream()
                .map(pblClassMapper::toSummaryDTO)
                .toList();
    }

    // Get available students for enrollment (filtered by majors)
    @Override
    public List<StudentSummaryDTO> getAvailableStudentsForClass(String pblClassId, Account account) {
        PblClass pblClass = findClassAndValidateAccess(pblClassId, account);

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

    // Get single class metadata
    @Override
    public PblClassResponseDTO getPblClassById(String PblClassId, Account account) {
        PblClass pblClass = findClassAndValidateAccess(PblClassId, account);
        return pblClassMapper.toResponseDTO(pblClass);
    }

    // Get enrolled students for a class
    @Override
    public List<StudentSummaryDTO> getEnrolledStudents(String PblClassId, Account account) {
        findClassAndValidateAccess(PblClassId, account);
        List<Student> students = pblClassRepository.findEnrolledStudentsByPblClassId(PblClassId);
        return students.stream()
                .map(studentMapper::toSummaryDTO)
                .toList();
    }

    // Get specific student details from a class (on click in tab 2)
    @Override
    public StudentResponseDTO getStudentInClass(String pblClassId, String studentId, Account account) {
        // Verify access to the class
        findClassAndValidateAccess(pblClassId, account);

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

    @Override
    public void addStudentsToClass(String pblClassId, List<String> studentIds, Account account) {
        PblClass pblClass = findClassAndValidateAccess(pblClassId, account);

        if (account.getRole() != UserRole.LECTURER) {
            throw new IllegalStateException("Only admins and lecturers can add students to class");
        }

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
                throw new ValidationException(
                        String.format("Student %s (%s) is not eligible for this class. Allowed majors: %s",
                                student.getFullName(),
                                student.getMajor().getName(),
                                allowedMajorIds)
                );
            }

            // Check if already enrolled
            if (enrollmentRepository.existsByStudentIdAndPblClassId(studentId, pblClassId)) {
                throw new ValidationException("Student " + studentId + " is already enrolled in this class");
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

    @Override
    public void removeStudentFromClass(String pblClassId, String studentId, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new IllegalStateException("Only lecturers can remove students from class");
        }

        PblClass pblClass = findClassAndValidateAccess(pblClassId, account);

        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndPblClassId(studentId, pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("Student not enrolled in this class"));

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

    // Update PBL class
    @Override
    public PblClassResponseDTO updatePblClass(String pblClassId, PblClassRequestDTO dto, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new IllegalStateException("Only lecturers can update PBL classes");
        }

        PblClass existingClass = findClassAndValidateAccess(pblClassId, account);

        // Prevent changing majors after creation
        if (dto.getMajorId() != null && !dto.getMajorId().isEmpty()) {
            List<String> existingMajorIds = existingClass.getMajors().stream()
                    .map(Major::getId)
                    .toList();
            List<String> newMajorIds = dto.getMajorId();

            if (!existingMajorIds.equals(newMajorIds)) {
                throw new ValidationException("Cannot change class majors after creation. Delete and recreate the class if needed.");
            }
        }

        // Validate group size change if present
        if (dto.getMaxStudentsPerGroup() != null) {
            validateGroupSizeChanges(existingClass, dto);
        }

        // Manual update for allowed fields only (don't use mapper for lecturer/department)
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

    // Delete PBL class
    @Override
    public void deletePblClass(String PblClassId, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new IllegalStateException("Only lecturers can delete class");
        }

        PblClass pblClass = pblClassRepository.findById(PblClassId)
                .orElseThrow(() -> new EntityNotFoundException("PBL class not found with ID: " + PblClassId));

        pblClassRepository.delete(pblClass);
    }

    // Helper method to validate access
    private PblClass findClassAndValidateAccess(String classId, Account account) {
        PblClass pblClass = pblClassRepository.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("PBL class not found with ID: " + classId));

        return switch (account.getRole()) {
            case ADMIN -> pblClass;
            case LECTURER -> {
                if (pblClass.getLecturer() != null && pblClass.getLecturer().getId().equals(account.getId())) {
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

    // Helper method to validate group size changes
    private void validateGroupSizeChanges(PblClass existingClass, PblClassRequestDTO requestDTO) {
        // Only validate if groups exist
        if (existingClass.getGroups() != null && !existingClass.getGroups().isEmpty()) {
            for (PblGroup group : existingClass.getGroups()) {
                int currentSize = group.getCurrentMemberCount();

                if (requestDTO.getMaxStudentsPerGroup() < currentSize) {
                    throw new ValidationException(
                            String.format("Cannot decrease maximum group size to %d because group '%s' currently has %d members",
                                    requestDTO.getMaxStudentsPerGroup(), group.getGroupName(), currentSize)
                    );
                }
            }
        }
    }

    // Additional utility methods
    public long getEnrolledStudentsCount(String PblClassId, Account account) {
        findClassAndValidateAccess(PblClassId, account);
        return pblClassRepository.countEnrolledStudentsByPblClassId(PblClassId);
    }
}
