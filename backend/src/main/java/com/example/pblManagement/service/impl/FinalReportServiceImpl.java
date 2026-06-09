package com.example.pblManagement.service.impl;

import com.example.pblManagement.mappers.FinalReportMapper;
import com.example.pblManagement.model.dto.finalreport.FileDownloadDTO;
import com.example.pblManagement.model.dto.finalreport.FinalReportRequestDTO;
import com.example.pblManagement.model.dto.finalreport.FinalReportResponseDTO;
import com.example.pblManagement.model.dto.finalreport.FinalReportSummaryDTO;
import com.example.pblManagement.model.entities.*;
import com.example.pblManagement.model.entities.enums.UserRole;
import com.example.pblManagement.repositories.FinalReportRepository;
import com.example.pblManagement.repositories.PblGroupRepository;
import com.example.pblManagement.repositories.StudentRepository;
import com.example.pblManagement.service.FileStorageService;
import com.example.pblManagement.service.FinalReportService;
import com.example.pblManagement.utils.PblClassAccessValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinalReportServiceImpl implements FinalReportService {
    private final FinalReportRepository finalReportRepository;
    private final PblGroupRepository pblGroupRepository;
    private final StudentRepository studentRepository;
    private final FinalReportMapper finalReportMapper;
    private final FileStorageService fileStorageService;
    private final PblClassAccessValidator pblClassAccessValidator;

    // Helper: Validate student is in a group for this class
    private PblGroup getStudentGroupInClass(String studentId, String pblClassId) {
        return pblGroupRepository.findStudentGroupInClass(studentId, pblClassId)
                .orElseThrow(() -> new AccessDeniedException("You must be in a group to submit final report"));
    }

    // Helper: Check if editing is allowed (not past deadline and not locked)
    private void validateDeadline(PblClass pblClass) {
        if (pblClass.getFinalReportDeadline() != null &&
                LocalDateTime.now().isAfter(pblClass.getFinalReportDeadline())) {
            throw new AccessDeniedException("Final report deadline has passed. Cannot submit or edit.");
        }
    }

    // Helper: Validate file type
    private void validateFileType(String fileName) {
        String fileType = fileStorageService.getFileType(fileName);
        if (!"PDF".equals(fileType) && !"DOCX".equals(fileType) && !"PPT".equals(fileType)) {
            throw new IllegalStateException("Invalid file type. Only PDF, DOCX, and PPT/PPTX files are allowed.");
        }
    }

    // Student: Submit their group's final report
    @Override
    @Transactional
    public FinalReportResponseDTO createOrUpdateReport(String pblClassId,
                                                       FinalReportRequestDTO dto,
                                                       MultipartFile file,
                                                       Account account) {
        // Only students can submit
        if (account.getRole() != UserRole.STUDENT) {
            throw new AccessDeniedException("Only students can submit final reports");
        }

        // Get class and validate deadline
        PblClass pblClass = pblClassAccessValidator.findClassAndValidateAccessAndReturnEntity(pblClassId, account);
        validateDeadline(pblClass);

        // Get student's group
        PblGroup group = getStudentGroupInClass(account.getId(), pblClassId);

        // Get student entity
        Student student = studentRepository.findById(account.getId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));

        // Validate file
        if (file == null || file.isEmpty()) {
            throw new IllegalStateException("File is required");
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

    // Student: View their own group's report
    @Override
    @Transactional(readOnly = true)
    public FinalReportResponseDTO getMyGroupReport(String pblClassId, Account account) {
        // Only students can view their own report
        if (account.getRole() != UserRole.STUDENT) {
            throw new AccessDeniedException("Only students can view their own report");
        }

        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        // Get student's group
        PblGroup group = getStudentGroupInClass(account.getId(), pblClassId);

        // Find report
        FinalReport report = finalReportRepository
                .findByGroupIdAndPblClassId(group.getId(), pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("No final report found for your group"));

        return finalReportMapper.toResponseDTO(report);
    }

    // Lecturer: Get the list of all submitted final reports of a specific class
    @Override
    @Transactional(readOnly = true)
    public List<FinalReportSummaryDTO> getAllReportsForClass(String pblClassId, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new AccessDeniedException("Only lecturers can view all final reports for the class");
        }

        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        List<FinalReport> reports = finalReportRepository.findByPblClassIdOrderBySubmittedAtDesc(pblClassId);

        return reports.stream()
                .map(finalReportMapper::toSummaryDTO)
                .toList();
    }

    // Lecturer: Get the details of a specific final report
    @Override
    @Transactional(readOnly = true)
    public FinalReportResponseDTO getReportById(Long reportId, String pblClassId, Account account) {
        if (account.getRole() != UserRole.LECTURER) {
            throw new AccessDeniedException("Only lecturers can view each individual final reports for the class");
        }

        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        FinalReport report = finalReportRepository.findByIdAndPblClassId(reportId, pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        return finalReportMapper.toResponseDTO(report);
    }

    // Download final report
    @Override
    @Transactional
    public FileDownloadDTO downloadReport(Long reportId, String pblClassId, Account account) {
        pblClassAccessValidator.findClassAndValidateAccess(pblClassId, account);

        // Fetch report once
        FinalReport report = finalReportRepository.findByIdAndPblClassId(reportId, pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        // Load file
        Resource resource = fileStorageService.loadFile(report.getFilePath());

        // Return both
        return FileDownloadDTO.builder()
                .resource(resource)
                .originalFileName(report.getOriginalFileName())
                .fileType(report.getFileType())
                .title(report.getTitle())
                .build();
    }

    // When a final report in the library receive bad perception, admin can permanently remove it
    @Override
    @Transactional
    public void adminDeleteReport(Long reportId, Account account) {
        // Only admins can perform this deletion
        if (account.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only admins can delete reports via this endpoint");
        }

        FinalReport report = finalReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        // Check deletion criteria: at least 10 ratings AND average rating < 2
        Integer ratingCount = report.getRatingCount();
        Double averageRating = report.getAverageRating();

        if (ratingCount == null || ratingCount < 10) {
            throw new IllegalStateException(
                    "Cannot delete: Report has only " + (ratingCount == null ? 0 : ratingCount) +
                            " ratings. Need at least 10 ratings for deletion."
            );
        }

        if (averageRating == null || averageRating >= 2.0) {
            throw new IllegalStateException(
                    "Cannot delete: Report average rating is " +
                            (averageRating == null ? "N/A" : String.format("%.2f", averageRating)) +
                            ". Need below 2.0 stars for deletion."
            );
        }

        // Delete the file
        fileStorageService.deleteFile(report.getFilePath());

        // Delete the report
        finalReportRepository.delete(report);
    }

    // Student: Toggle public status of their group's report
    @Transactional
    @Override
    public void togglePublicStatus(Long reportId, String pblClassId, Account account) {
        // Only students can toggle
        if (account.getRole() != UserRole.STUDENT) {
            throw new AccessDeniedException("Only students can change public status of their reports");
        }

        // Get the report
        FinalReport report = finalReportRepository.findByIdAndPblClassId(reportId, pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        // Verify student is in the group that owns this report
        PblGroup group = getStudentGroupInClass(account.getId(), pblClassId);

        if (!report.getGroup().getId().equals(group.getId())) {
            throw new AccessDeniedException("You can only change public status for your own group's report");
        }

        // Toggle the public status
        report.setPublic(!report.isPublic());
        finalReportRepository.save(report);
    }
}
