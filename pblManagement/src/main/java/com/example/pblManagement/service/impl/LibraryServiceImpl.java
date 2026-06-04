package com.example.pblManagement.service.impl;

import com.example.pblManagement.model.dto.finalreport.FileDownloadDTO;
import com.example.pblManagement.model.dto.finalreport.LibraryReportResponseDTO;
import com.example.pblManagement.model.dto.finalreport.LibrarySearchRequestDTO;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.model.entities.FinalReport;
import com.example.pblManagement.model.entities.ReportRating;
import com.example.pblManagement.model.entities.enums.UserRole;
import com.example.pblManagement.repositories.FinalReportRepository;
import com.example.pblManagement.repositories.ReportRatingRepository;
import com.example.pblManagement.service.FileStorageService;
import com.example.pblManagement.service.LibraryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {
    private final FinalReportRepository finalReportRepository;
    private final ReportRatingRepository reportRatingRepository;
    private final FileStorageService fileStorageService;

    // Helper: Convert entity to public DTO (anonymous)
    private LibraryReportResponseDTO toLibraryDTO(FinalReport report) {
        String className = report.getPblClass() != null ? report.getPblClass().getClassName() : null;
        String departmentName = null;

        if (report.getPblClass() != null && report.getPblClass().getLecturer() != null &&
                report.getPblClass().getLecturer().getDepartment() != null) {
            departmentName = report.getPblClass().getLecturer().getDepartment().getName();
        }

        return LibraryReportResponseDTO.builder()
                .id(report.getId())
                .title(report.getTitle())
                .description(report.getDescription())
                .fileType(report.getFileType())
                .className(className)
                .departmentName(departmentName)
                .averageRating(report.getAverageRating())
                .ratingCount(report.getRatingCount())
                .downloadCount(report.getDownloadCount())
                .submittedAt(report.getSubmittedAt())
                .build();
    }

    // All roles: Search for a report
    @Override
    @Transactional
    public Page<LibraryReportResponseDTO> searchReports(LibrarySearchRequestDTO searchRequest) {
        // Build sort order
        Sort sort = switch (searchRequest.getSortBy()) {
            case "highest_rated" -> Sort.by(Sort.Direction.DESC, "averageRating");
            case "most_downloaded" -> Sort.by(Sort.Direction.DESC, "downloadCount");
            default -> Sort.by(Sort.Direction.DESC, "submittedAt");
        };

        PageRequest pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);

        // Use repository method with filters
        Page<FinalReport> reports = finalReportRepository.findPublicReports(
                searchRequest.getKeyword(),
                searchRequest.getClassName(),
                searchRequest.getDepartmentId(),
                searchRequest.getFileType(),
                pageable
        );

        return reports.map(this::toLibraryDTO);
    }

    // All roles: See details of a public report
    @Override
    @Transactional(readOnly = true)
    public LibraryReportResponseDTO getReportDetails(Long reportId) {
        FinalReport report = finalReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        if (!report.isPublic()) {
            throw new EntityNotFoundException("Report not found");
        }

        return toLibraryDTO(report);
    }

    // All roles: Download public reports from the library
    @Override
    @Transactional
    public FileDownloadDTO downloadReport(Long reportId) {
        FinalReport report = finalReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        if (!report.isPublic()) {
            throw new EntityNotFoundException("Report not found");
        }

        // Load file FIRST to verify it exists and is readable
        Resource fileResource = fileStorageService.loadFile(report.getFilePath());

        // Only increment download count if file loads successfully
        report.setDownloadCount((report.getDownloadCount() != null ? report.getDownloadCount() : 0) + 1);
        finalReportRepository.save(report);

        // Return both
        return FileDownloadDTO.builder()
                .resource(fileResource)
                .originalFileName(report.getOriginalFileName())
                .fileType(report.getFileType())
                .title(report.getTitle())
                .build();
    }

    // All roles: Rate someone's final report
    @Override
    @Transactional
    public void rateReport(Long reportId, Integer rating, Account account) {
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }

        FinalReport report = finalReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        if (!report.isPublic()) {
            throw new EntityNotFoundException("Report not found");
        }

        // Check if user already rated this report
        ReportRating existingRating = reportRatingRepository
                .findByReportIdAndUserId(reportId, account.getId())
                .orElse(null);

        if (existingRating != null) {
            existingRating.setRating(rating);
            reportRatingRepository.save(existingRating);
        } else {
            // Create new rating
            ReportRating newRating = ReportRating.builder()
                    .report(report)
                    .userId(account.getId())
                    .rating(rating)
                    .build();
            reportRatingRepository.save(newRating);
        }

        report.recalculateAverageRating();
        finalReportRepository.save(report);
    }

    // All roles: Get ratings of one report
    @Override
    @Transactional(readOnly = true)
    public Integer getUserRating(Long reportId, Account account) {
        if (account == null) return null;

        return reportRatingRepository.findByReportIdAndUserId(reportId, account.getId())
                .map(ReportRating::getRating)
                .orElse(null);
    }

    // Student: Toggle public status of their group's report
    @Override
    @Transactional
    public void togglePublicStatus(Long reportId, String pblClassId, Account account) {
        // Only students can toggle their own report's public status
        if (account.getRole() != UserRole.STUDENT) {
            throw new IllegalStateException("Only students can change public status of their reports");
        }

        FinalReport report = finalReportRepository.findByIdAndPblClassId(reportId, pblClassId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        // Verify student owns this report (via group membership)
        boolean isOwner = report.getGroup().getEnrollments().stream()
                .anyMatch(e -> e.getStudent().getId().equals(account.getId()));

        if (!isOwner) {
            throw new AccessDeniedException("You can only change public status for your own group's report");
        }

        report.setPublic(!report.isPublic());
        finalReportRepository.save(report);
    }
}
