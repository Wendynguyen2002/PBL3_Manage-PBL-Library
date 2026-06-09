package com.example.pblManagement.service.impl;
import com.example.pblManagement.mappers.LecturerMapper;
import com.example.pblManagement.model.dto.common.PasswordChangeDTO;
import com.example.pblManagement.model.dto.user.*;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.model.entities.Lecturer;
import com.example.pblManagement.model.entities.enums.UserRole;
import com.example.pblManagement.repositories.DepartmentRepository;
import com.example.pblManagement.repositories.LecturerRepository;
import com.example.pblManagement.service.LecturerService;
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
public class LecturerServiceImpl implements LecturerService {
    private final LecturerRepository lecturerRepository;
    private final LecturerMapper lecturerMapper;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;

    // Admin: Create new lecturer
    @Transactional
    @Override
    public LecturerResponseDTO createLecturer(LecturerRequestDTO dto) {
        securityUtils.verifyAdmin();

        // Verify department exists
        if (!departmentRepository.existsById(dto.getDepartmentId())) {
            throw new EntityNotFoundException("Department not found");
        }

        // Auto generate email from lecturer ID
        String generatedEmail = generateLecturerEmail(dto.getId());
        String generatedPassword = generateLecturerPassword(dto.getId());

        // Create lecturer entity
        Lecturer lecturer = lecturerMapper.toEntity(dto);
        lecturer.setEmail(generatedEmail);
        lecturer.setPassword(passwordEncoder.encode(generatedPassword));
        lecturer.setRole(UserRole.LECTURER);

        return lecturerMapper.toResponseDTO(lecturerRepository.save(lecturer));
    }

    // Admin: Get lecturer by ID with full details
    @Transactional(readOnly = true)
    @Override
    public LecturerResponseDTO getLecturerById(String id) {
        securityUtils.verifyAdmin();

        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lecturer not found"));
        return lecturerMapper.toResponseDTO(lecturer);
    }

    // Admin: Get all lecturers with search and pagination
    @Transactional(readOnly = true)
    @Override
    public Page<LecturerSummaryDTO> getAllLecturers(String search, Pageable pageable) {
        securityUtils.verifyAdmin();

        Page<Lecturer> lecturersPage;

        if (search == null || search.trim().isEmpty()) {
            lecturersPage = lecturerRepository.findAll(pageable);
        } else {
            lecturersPage = lecturerRepository.searchLecturers(search.trim(), pageable);
        }

        return lecturersPage.map(lecturerMapper::toSummaryDTO);
    }

    // Admin: Update lecturer
    @Transactional
    @Override
    public LecturerResponseDTO updateLecturer(String id, LecturerRequestDTO dto) {
        securityUtils.verifyAdmin();

        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lecturer not found"));

        // Verify department exists
        if (!departmentRepository.existsById(dto.getDepartmentId())) {
            throw new EntityNotFoundException("Department not found");
        }

        lecturerMapper.updateLecturer(lecturer, dto);

        return lecturerMapper.toResponseDTO(lecturerRepository.save(lecturer));
    }

    // Admin: Delete lecturer
    @Transactional
    @Override
    public void deleteLecturer(String id) {
        securityUtils.verifyAdmin();

        if (!lecturerRepository.existsById(id)) {
            throw new EntityNotFoundException("Lecturer not found with id: " + id);
        }
        lecturerRepository.deleteById(id);
    }

    // Admin: Reset lecturer password to default format
    @Transactional
    @Override
    public void resetLecturerPassword(String id) {
        securityUtils.verifyAdmin();

        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lecturer not found"));

        // Reset to original password format: Udn@{lecturerId}
        String defaultPassword = generateLecturerPassword(lecturer.getId());
        lecturer.setPassword(passwordEncoder.encode(defaultPassword));
        lecturerRepository.save(lecturer);
    }

    // Lecturer: Update own profile
    @Transactional
    @Override
    public LecturerResponseDTO updateOwnProfile(LecturerSelfUpdateRequestDTO dto, Account account) {
        Lecturer lecturer = (Lecturer) account;

        lecturerMapper.updateSelf(lecturer, dto);

        Lecturer updated = lecturerRepository.save(lecturer);
        return lecturerMapper.toResponseDTO(updated);
    }

    // Lecturer: Change own password
    @Transactional
    @Override
    public void changePassword(PasswordChangeDTO dto, Account account) {
        Lecturer lecturer = (Lecturer) account;

        // Verify current password
        if (!passwordEncoder.matches(dto.getCurrentPassword(), lecturer.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Set new password
        lecturer.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        lecturerRepository.save(lecturer);
    }

    // Lecturer: Get own profile
    @Transactional(readOnly = true)
    @Override
    public LecturerResponseDTO getOwnProfile(Account account) {
        Lecturer lecturer = (Lecturer) account;
        return lecturerMapper.toResponseDTO(lecturer);
    }

    // Helper method to generate email
    private String generateLecturerEmail(String lecturerId) {
        // Format: {lecturerId}gv@dut.udn.vn
        return (lecturerId + "gv@dut.udn.vn").trim();
    }

    // Helper method to generate default password
    private String generateLecturerPassword(String lecturerId) {
        // Format: Udn@{LecturerId}
        return ("Udn@" + lecturerId).trim();
    }
}
