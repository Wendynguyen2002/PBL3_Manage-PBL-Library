package com.example.pblManagement.service.impl;
import com.example.pblManagement.mappers.PblClassMapper;
import com.example.pblManagement.mappers.StudentMapper;
import com.example.pblManagement.model.dto.pbl.PblClassRequestDTO;
import com.example.pblManagement.model.dto.pbl.PblClassResponseDTO;
import com.example.pblManagement.model.dto.pbl.PblClassSummaryDTO;
import com.example.pblManagement.model.dto.user.StudentResponseDTO;
import com.example.pblManagement.model.dto.user.StudentSummaryDTO;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.model.entities.PblClass;
import com.example.pblManagement.model.entities.PblGroup;
import com.example.pblManagement.model.entities.Student;
import com.example.pblManagement.model.entities.enums.UserRole;
import com.example.pblManagement.repositories.LecturerRepository;
import com.example.pblManagement.repositories.PblClassRepository;
import com.example.pblManagement.repositories.StudentRepository;
import com.example.pblManagement.service.PblClassService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PblClassServiceImpl implements PblClassService {
    private final PblClassRepository pblClassRepository;
    private final PblClassMapper pblClassMapper;
    private final LecturerRepository lecturerRepository;
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    // Admin + lecturer: Create PBL class
    @Override
    public PblClassResponseDTO createPblClass(PblClassRequestDTO dto, Account account) {
        if (account.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("Only admin can create PBL class");
        }

        // Check if id already exists
        if (pblClassRepository.existsById(dto.getId())) {
            throw new ValidationException("PBL class ID already exists");
        }

        // Check if lecturer exists
        if (!lecturerRepository.existsById(dto.getLecturerId())) {
            throw new IllegalArgumentException("Lecturer not found");
        }

        // Validate group size constraints
        if (dto.getMinStudentsPerGroup() > dto.getMaxStudentsPerGroup()) {
            throw new ValidationException("Minimum students per group cannot be greater than maximum");
        }

        PblClass pblClass = pblClassMapper.toEntity(dto);

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

    // Get single class metadata (tab 1)
    @Override
    public PblClassResponseDTO getPblClassById(String PblClassId, Account account) {
        PblClass pblClass = findClassAndValidateAccess(PblClassId, account);
        return pblClassMapper.toResponseDTO(pblClass);
    }

    // Get enrolled students for a clas (tab 2)
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

    // Update PBL class
    @Override
    public PblClassResponseDTO updatePblClass(String pblClassId, PblClassRequestDTO dto, Account account) {
        if (account.getRole() != UserRole.ADMIN && account.getRole() != UserRole.LECTURER) {
            throw new IllegalStateException("Only admins and lecturers can update PBL classes");
        }
        PblClass existingClass = findClassAndValidateAccess(pblClassId, account);

        if (dto.getMinStudentsPerGroup() > dto.getMaxStudentsPerGroup()) {
            throw new ValidationException("Minimum students per group cannot be greater than maximum");
        }

        pblClassMapper.updatePblClass(existingClass, dto);
        return pblClassMapper.toResponseDTO(pblClassRepository.save(existingClass));
    }

    // Delete PBL class
    @Override
    public void deletePblClass(String PblClassId, Account account) {
        if (account.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("Only admin can delete class");
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
            default -> throw new IllegalStateException("Invalid user role");
        };
    }

    // Helper method to validate group size changes
    private void validateGroupSizeChanges(PblClass existingClass, PblClassRequestDTO requestDTO) {
        // Only validate if groups exist
        if (existingClass.getGroups() != null && !existingClass.getGroups().isEmpty()) {
            for (PblGroup group : existingClass.getGroups()) {
                int currentSize = group.getCurrentMemberCount();

                if (requestDTO.getMinStudentsPerGroup() > currentSize) {
                    throw new ValidationException(
                            String.format("Cannot increase minimum group size to %d because group '%s' currently has %d members",
                                    requestDTO.getMinStudentsPerGroup(), group.getGroupName(), currentSize)
                    );
                }

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
