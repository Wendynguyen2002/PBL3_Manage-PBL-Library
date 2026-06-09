package com.example.pblManagement.service.impl;
import com.example.pblManagement.exceptions.InvalidCurrentPasswordException;
import com.example.pblManagement.mappers.StudentMapper;
import com.example.pblManagement.model.dto.common.PasswordChangeDTO;
import com.example.pblManagement.model.dto.user.*;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.model.entities.Student;
import com.example.pblManagement.model.entities.enums.UserRole;
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
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final MajorRepository majorRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;

    // Admin: Create new student
    @Transactional
    @Override
    public StudentResponseDTO createStudent(StudentRequestDTO dto) {
        securityUtils.verifyAdmin();

        // Verify major exists
        if (!majorRepository.existsById(dto.getMajorId())) {
            throw new EntityNotFoundException("Major not found");
        }

        // Auto-generate email and password from student ID
        String generatedEmail = generateStudentEmail(dto.getId());
        String generatedPassword = generateStudentPassword(dto.getId());

        // Create student entity
        Student student = studentMapper.toEntity(dto);
        student.setEmail(generatedEmail);
        student.setPassword(passwordEncoder.encode(generatedPassword));
        student.setRole(UserRole.STUDENT);

        return studentMapper.toResponseDTO(studentRepository.save(student));
    }

    // Admin: Get student by ID with full details
    @Transactional(readOnly = true)
    @Override
    public StudentResponseDTO getStudentById(String id) {
        securityUtils.verifyAdmin();

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        return studentMapper.toResponseDTO(student);
    }

    // Admin: Get all students with search and pagination
    @Transactional(readOnly = true)
    @Override
    public Page<StudentSummaryDTO> getAllStudents(String search, Pageable pageable) {
        securityUtils.verifyAdmin();

        Page<Student> studentsPage;

        if (search == null || search.trim().isEmpty()) {
            studentsPage = studentRepository.findAll(pageable);
        } else {
            studentsPage = studentRepository.searchStudents(search.trim(), pageable);
        }

        return studentsPage.map(studentMapper::toSummaryDTO);
    }

    // Admin: Update student
    @Transactional
    @Override
    public StudentResponseDTO updateStudent(String id, StudentRequestDTO dto) {
        securityUtils.verifyAdmin();

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        // Verify major exists
        if (!majorRepository.existsById(dto.getMajorId())) {
            throw new EntityNotFoundException("Major not found");
        }

        studentMapper.updateStudent(student, dto);

        return studentMapper.toResponseDTO(studentRepository.save(student));
    }

    // Admin: Delete student
    @Transactional
    @Override
    public void deleteStudent(String id) {
        securityUtils.verifyAdmin();

        if (!studentRepository.existsById(id)) {
            throw new EntityNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    // Admin: Reset password to the original format in case this student forgot
    @Transactional
    @Override
    public void resetStudentPassword(String id) {
        securityUtils.verifyAdmin();

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));

        // Reset to original password format: Dut@{studentId}
        String defaultPassword = generateStudentPassword(student.getId());
        student.setPassword(passwordEncoder.encode(defaultPassword));
        studentRepository.save(student);
    }

    // Student: Update own profile
    @Transactional
    @Override
    public StudentResponseDTO updateOwnProfile(StudentSelfUpdateRequestDTO dto, Account account) {
        Student student = (Student) account;

        studentMapper.updateSelf(student, dto);
        Student updated = studentRepository.save(student);
        return studentMapper.toResponseDTO(updated);
    }

    // Student: Change own password
    @Transactional
    @Override
    public void changePassword(PasswordChangeDTO dto, Account account) {
        Student student = (Student) account;

        // Verify current password
        if (!passwordEncoder.matches(dto.getCurrentPassword(), student.getPassword())) {
            throw new InvalidCurrentPasswordException("Current password is incorrect");
        }

        // Set new password
        student.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        studentRepository.save(student);
    }

    // Student: Get own profile
    @Transactional(readOnly = true)
    @Override
    public StudentResponseDTO getOwnProfile(Account account) {
        Student student = (Student) account;
        return studentMapper.toResponseDTO(student);
    }

    // Helper method to generate email
    private String generateStudentEmail(String studentId) {
        // Format: {studentId}sv@dut.udn.vn
        return (studentId + "sv@dut.udn.vn").trim();
    }

    // Helper method to generate default password
    private String generateStudentPassword(String studentId) {
        // Format: Dut@{studentId}
        return ("Dut@" + studentId).trim();
    }
}
