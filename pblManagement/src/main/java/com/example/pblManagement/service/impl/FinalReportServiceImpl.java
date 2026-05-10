package com.example.pblManagement.service.impl;

import com.example.pblManagement.mappers.FinalReportMapper;
import com.example.pblManagement.model.dto.finalreport.FinalReportRequestDTO;
import com.example.pblManagement.model.dto.finalreport.FinalReportResponseDTO;
import com.example.pblManagement.model.dto.finalreport.FinalReportSummaryDTO;
import com.example.pblManagement.model.entities.*;
import com.example.pblManagement.model.entities.enums.UserRole;
import com.example.pblManagement.repositories.FinalReportRepository;
import com.example.pblManagement.repositories.PblClassRepository;
import com.example.pblManagement.repositories.PblGroupRepository;
import com.example.pblManagement.repositories.StudentRepository;
import com.example.pblManagement.service.FileStorageService;
import com.example.pblManagement.service.FinalReportService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FinalReportServiceImpl implements FinalReportService {
    private final FinalReportRepository finalReportRepository;
    private final PblClassRepository pblClassRepository;
    private final PblGroupRepository pblGroupRepository;
    private final StudentRepository studentRepository;
    private final FinalReportMapper finalReportMapper;
    private final FileStorageService fileStorageService;

    // Helper: Validate student is in a group for this class
    private PblGroup getStudentGroupInClass(String studentId, String pblClassId) {
        return pblGroupRepository.findStudentGroupInClass(studentId, pblClassId)
                .orElseThrow(() -> new ValidationException("You must be in a group to submit final report"));
    }

    // Helper: Validate class exists and return it
    private PblClass validateClassExists(String pblClassId) {
        return pblClassRepository.findById(pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("PBL class not found: " + pblClassId));
    }

    // Helper: Validate class access for lecturer
    private void validateLecturerAccess(String pblClassId, Account account) {
        if (account.getRole() != UserRole.LECTURER && account.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("Only lecturers can view all reports");
        }

        PblClass pblClass = validateClassExists(pblClassId);

        if (account.getRole() == UserRole.LECTURER) {
            if (pblClass.getLecturer() == null || !pblClass.getLecturer().getId().equals(account.getId())) {
                throw new IllegalStateException("You don't have access to this class");
            }
        }
    }

    // Helper: Check if editing is allowed (not past deadline and not locked)
    private void validateDeadline(PblClass pblClass) {
        if (pblClass.isFinalReportLocked()) {
            throw new ValidationException("Final reports are locked. Cannot submit or edit.");
        }

        if (pblClass.getFinalReportDeadline() != null &&
                LocalDateTime.now().isAfter(pblClass.getFinalReportDeadline())) {
            throw new ValidationException("Final report deadline has passed. Cannot submit or edit.");
        }
    }

    // Helper: Validate file type
    private void validateFileType(String fileName) {
        String fileType = fileStorageService.getFileType(fileName);
        if (!"PDF".equals(fileType) && !"DOCX".equals(fileType) && !"PPT".equals(fileType)) {
            throw new ValidationException("Invalid file type. Only PDF, DOCX, and PPT/PPTX files are allowed.");
        }
    }

    @Override
    public FinalReportResponseDTO createOrUpdateReport(String pblClassId,
                                                       FinalReportRequestDTO dto,
                                                       MultipartFile file,
                                                       Account account) throws Exception {
        // Only students can submit
        if (account.getRole() != UserRole.STUDENT) {
            throw new IllegalStateException("Only students can submit final reports");
        }

        // Verify student is enrolled in class
        if (!pblClassRepository.isStudentEnrolledInClass(pblClassId, account.getId())) {
            throw new ValidationException("You are not enrolled in this class");
        }

        // Get class and validate deadline
        PblClass pblClass = validateClassExists(pblClassId);
        validateDeadline(pblClass);

        // Get student's group
        PblGroup group = getStudentGroupInClass(account.getId(), pblClassId);

        // Get student entity
        Student student = studentRepository.findById(account.getId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        // Validate file
        if (file == null || file.isEmpty()) {
            throw new ValidationException("File is required");
        }
        validateFileType(file.getOriginalFilename());

        // Check if report already exists
        FinalReport existingReport = finalReportRepository
                .findByGroupIdAndPblClassId(group.getId(), pblClassId)
                .orElse(null);

        // Store the file
        String filePath = fileStorageService.storeFile(file, pblClassId, group.getId());
        String fileType = fileStorageService.getFileType(file.getOriginalFilename());

        FinalReport report;
        boolean isNew = (existingReport == null);

        if (isNew) {
            // Create new report
            report = finalReportMapper.toEntity(dto);
            report.setGroup(group);
            report.setPblClass(pblClass);
            report.setSubmittedBy(student);
            report.setFilePath(filePath);
            report.setFileType(fileType);
            report.setOriginalFileName(file.getOriginalFilename());

            report = finalReportRepository.save(report);
        } else {
            // Update existing report
            // Delete old file
            fileStorageService.deleteFile(existingReport.getFilePath());

            // Update fields
            existingReport.setTitle(dto.getTitle());
            existingReport.setDescription(dto.getDescription());
            existingReport.setFilePath(filePath);
            existingReport.setFileType(fileType);
            existingReport.setOriginalFileName(file.getOriginalFilename());
            existingReport.setLastModifiedBy(student);

            report = finalReportRepository.save(existingReport);
        }

        return finalReportMapper.toResponseDTO(report);
    }

    @Override
    public FinalReportResponseDTO getMyGroupReport(String pblClassId, Account account) {
        // Only students can view their own report
        if (account.getRole() != UserRole.STUDENT) {
            throw new IllegalStateException("Only students can view their own report");
        }

        // Verify student is enrolled
        if (!pblClassRepository.isStudentEnrolledInClass(pblClassId, account.getId())) {
            throw new ValidationException("You are not enrolled in this class");
        }

        // Get student's group
        PblGroup group = getStudentGroupInClass(account.getId(), pblClassId);

        // Find report
        FinalReport report = finalReportRepository
                .findByGroupIdAndPblClassId(group.getId(), pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("No final report found for your group"));

        return finalReportMapper.toResponseDTO(report);
    }

    @Override
    public List<FinalReportSummaryDTO> getAllReportsForClass(String pblClassId, Account account) {
        validateLecturerAccess(pblClassId, account);

        List<FinalReport> reports = finalReportRepository.findByPblClassIdOrderBySubmittedAtDesc(pblClassId);

        return reports.stream()
                .map(finalReportMapper::toSummaryDTO)
                .toList();
    }

    @Override
    public FinalReportResponseDTO getReportById(Long reportId, String pblClassId, Account account) {
        // Allow: Lecturer of class OR student who owns the report's group
        validateClassExists(pblClassId);

        FinalReport report = finalReportRepository.findByIdAndPblClassId(reportId, pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        // Check permissions
        if (account.getRole() == UserRole.LECTURER || account.getRole() == UserRole.ADMIN) {
            validateLecturerAccess(pblClassId, account);
        } else if (account.getRole() == UserRole.STUDENT) {
            // Check if student belongs to the group that owns this report
            PblGroup studentsGroup = getStudentGroupInClass(account.getId(), pblClassId);
            if (!studentsGroup.getId().equals(report.getGroup().getId())) {
                throw new IllegalStateException("You don't have access to this report");
            }
        } else {
            throw new IllegalStateException("Invalid user role");
        }

        return finalReportMapper.toResponseDTO(report);
    }

    @Override
    public Resource downloadReport(Long reportId, String pblClassId, Account account) throws Exception {
        // Reuse permission logic from getReportById
        FinalReportResponseDTO report = getReportById(reportId, pblClassId, account);

        return fileStorageService.loadFile(report.getFilePath());
    }

    @Override
    public void deleteReport(Long reportId, String pblClassId, Account account) throws Exception {
        // Only lecturers and admins can delete
        if (account.getRole() != UserRole.LECTURER && account.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("Only lecturers can delete reports");
        }

        validateLecturerAccess(pblClassId, account);

        FinalReport report = finalReportRepository.findByIdAndPblClassId(reportId, pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        // Delete the file
        fileStorageService.deleteFile(report.getFilePath());

        // Delete the record
        finalReportRepository.delete(report);
    }

    @Override
    public void adminDeleteReport(Long reportId, Account account) throws Exception {
        // Only admins can perform this deletion
        if (account.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("Only admins can delete reports via this endpoint");
        }

        FinalReport report = finalReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        // Check deletion criteria: at least 10 ratings AND average rating < 2
        Integer ratingCount = report.getRatingCount();
        Double averageRating = report.getAverageRating();

        if (ratingCount == null || ratingCount < 10) {
            throw new ValidationException(
                    "Cannot delete: Report has only " + (ratingCount == null ? 0 : ratingCount) +
                            " ratings. Need at least 10 ratings for deletion."
            );
        }

        if (averageRating == null || averageRating >= 2.0) {
            throw new ValidationException(
                    "Cannot delete: Report average rating is " +
                            (averageRating == null ? "N/A" : String.format("%.2f", averageRating)) +
                            ". Need below 2.0 stars for deletion."
            );
        }

        // Delete the file
        fileStorageService.deleteFile(report.getFilePath());

        // Delete ratings first (due to FK constraint)
        // This will be handled by CASCADE DELETE if you added it to the relationship
        // If not, you may need to delete ratings manually

        // Delete the report
        finalReportRepository.delete(report);
    }
}
