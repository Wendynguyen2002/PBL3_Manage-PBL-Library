package com.example.pblManagement.service.impl;
import com.example.pblManagement.mappers.StudentMapper;
import com.example.pblManagement.model.dto.common.PasswordChangeDTO;
import com.example.pblManagement.model.dto.user.*;
import com.example.pblManagement.model.entities.Student;
import com.example.pblManagement.repositories.MajorRepository;
import com.example.pblManagement.repositories.StudentRepository;
import com.example.pblManagement.service.StudentService;
import com.example.pblManagement.utils.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final MajorRepository majorRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;

    // Admin: Create new student
    @Override
    public StudentResponseDTO createStudent(StudentRequestDTO dto) {
        // Check if mail already exists
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Verify major exists
        if (!majorRepository.existsById(dto.getMajorId())) {
            throw new IllegalArgumentException("Major not found");
        }

        Student student = studentMapper.toEntity(dto);

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            student.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return studentMapper.toResponseDTO(studentRepository.save(student));
    }

    // Admin: Get student by ID with full details
    @Override
    public StudentResponseDTO getStudentById(String id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("student not found"));
        return studentMapper.toResponseDTO(student);
    }

    // Admin: Get all students with search and pagination
    @Override
    public Page<StudentSummaryDTO> getAllStudents(String search, Pageable pageable) {
        Page<Student> studentsPage;

        if (search == null || search.trim().isEmpty()) {
            studentsPage = studentRepository.findAll(pageable);
        } else {
            studentsPage = studentRepository.searchStudents(search.trim(), pageable);
        }

        return studentsPage.map(studentMapper::toSummaryDTO);
    }

    // Admin: Update student
    @Override
    public StudentResponseDTO updateStudent(String id, StudentRequestDTO dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("student not found"));

        // Check email uniqueness if changed
        if (!student.getEmail().equals(dto.getEmail()) && studentRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Verify major exists
        if (!majorRepository.existsById(dto.getMajorId())) {
            throw new EntityNotFoundException("major not found");
        }

        studentMapper.updateStudent(student, dto);

        // Only update password if provided
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            student.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return studentMapper.toResponseDTO(studentRepository.save(student));
    }

    // Admin: Delete student
    @Override
    public void deleteStudent(String id) {
        if (!studentRepository.existsById(id)) {
            throw new EntityNotFoundException("student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    // student: Update own profile
    @Override
    public StudentResponseDTO updateOwnProfile(StudentSelfUpdateRequestDTO dto) {
        String currentStudentId = securityUtils.getCurrentUserId();
        Student student = studentRepository.findById(currentStudentId)
                .orElseThrow(() -> new EntityNotFoundException("student not found"));

        studentMapper.updateSelf(student, dto);

        Student updated = studentRepository.save(student);
        return studentMapper.toResponseDTO(updated);
    }

    // Separate endpoint for password change
    @Override
    public void changePassword(PasswordChangeDTO dto) {
        String currentStudentId = securityUtils.getCurrentUserId();
        Student student = studentRepository.findById(currentStudentId)
                .orElseThrow(() -> new EntityNotFoundException("student not found"));

        // Verify current password
        if (!passwordEncoder.matches(dto.getCurrentPassword(), student.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Set new password
        student.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        studentRepository.save(student);
    }

    // student: Get own profile
    @Override
    public StudentResponseDTO getOwnProfile() {
        String currentUserId = securityUtils.getCurrentUserId();
        Student student = studentRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("student not found"));
        return studentMapper.toResponseDTO(student);
    }
}
