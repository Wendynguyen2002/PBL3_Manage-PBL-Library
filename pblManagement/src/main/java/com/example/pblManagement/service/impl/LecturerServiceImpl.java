package com.example.pblManagement.service.impl;
import com.example.pblManagement.mappers.LecturerMapper;
import com.example.pblManagement.model.dto.common.PasswordChangeDTO;
import com.example.pblManagement.model.dto.user.*;
import com.example.pblManagement.model.entities.Lecturer;
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
@Transactional
public class LecturerServiceImpl implements LecturerService {
    private final LecturerRepository lecturerRepository;
    private final LecturerMapper lecturerMapper;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;

    // Admin: Create new lecturer
    @Override
    public LecturerResponseDTO createLecturer(LecturerRequestDTO dto) {
        // Check if mail already exists
        if (lecturerRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Verify department exists
        if (!departmentRepository.existsById(dto.getDepartmentId())) {
            throw new IllegalArgumentException("Department not found");
        }

        Lecturer lecturer = lecturerMapper.toEntity(dto);

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            lecturer.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            lecturer.setPassword(passwordEncoder.encode(dto.getId())); // Default password is the lecturer's ID
        }

        return lecturerMapper.toResponseDTO(lecturerRepository.save(lecturer));
    }

    // Admin: Get lecturer by ID with full details
    @Override
    public LecturerResponseDTO getLecturerById(String id) {
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lecturer not found"));
        return lecturerMapper.toResponseDTO(lecturer);
    }

    // Admin: Get all lecturers with search and pagination
    @Override
    public Page<LecturerSummaryDTO> getAllLecturers(String search, Pageable pageable) {
        Page<Lecturer> lecturersPage;

        if (search == null || search.trim().isEmpty()) {
            lecturersPage = lecturerRepository.findAll(pageable);
        } else {
            lecturersPage = lecturerRepository.searchLecturers(search.trim(), pageable);
        }

        return lecturersPage.map(lecturerMapper::toSummaryDTO);
    }

    // Admin: Update lecturer
    @Override
    public LecturerResponseDTO updateLecturer(String id, LecturerRequestDTO dto) {
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lecturer not found"));

        // Check email uniqueness if changed
        if (!lecturer.getEmail().equals(dto.getEmail()) && lecturerRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Verify department exists
        if (!departmentRepository.existsById(dto.getDepartmentId())) {
            throw new EntityNotFoundException("Department not found");
        }

        lecturerMapper.updateLecturer(lecturer, dto);

        // Only update password if provided
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            lecturer.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return lecturerMapper.toResponseDTO(lecturerRepository.save(lecturer));
    }

    // Admin: Delete lecturer
    @Override
    public void deleteLecturer(String id) {
        if (!lecturerRepository.existsById(id)) {
            throw new EntityNotFoundException("Lecturer not found with id: " + id);
        }
        lecturerRepository.deleteById(id);
    }

    // Lecturer: Update own profile
    @Override
    public LecturerResponseDTO updateOwnProfile(LecturerSelfUpdateRequestDTO dto) {
        String currentLecturerId = securityUtils.getCurrentUserId();
        Lecturer lecturer = lecturerRepository.findById(currentLecturerId)
                .orElseThrow(() -> new EntityNotFoundException("Lecturer not found"));

        lecturerMapper.updateSelf(lecturer, dto);

        Lecturer updated = lecturerRepository.save(lecturer);
        return lecturerMapper.toResponseDTO(updated);
    }

    // Separate endpoint for password change
    @Override
    public void changePassword(PasswordChangeDTO dto) {
        String currentLecturerId = securityUtils.getCurrentUserId();
        Lecturer lecturer = lecturerRepository.findById(currentLecturerId)
                .orElseThrow(() -> new EntityNotFoundException("Lecturer not found"));

        // Verify current password
        if (!passwordEncoder.matches(dto.getCurrentPassword(), lecturer.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Set new password
        lecturer.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        lecturerRepository.save(lecturer);
    }

    // Lecturer: Get own profile
    @Override
    public LecturerResponseDTO getOwnProfile() {
        String currentUserId = securityUtils.getCurrentUserId();
        Lecturer lecturer = lecturerRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Lecturer not found"));
        return lecturerMapper.toResponseDTO(lecturer);
    }

}
