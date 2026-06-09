package com.example.pblManagement.controllers;

import com.example.pblManagement.model.dto.finalreport.FileDownloadDTO;
import com.example.pblManagement.model.dto.finalreport.FinalReportRequestDTO;
import com.example.pblManagement.model.dto.finalreport.FinalReportResponseDTO;
import com.example.pblManagement.model.dto.finalreport.FinalReportSummaryDTO;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.security.CurrentUser;
import com.example.pblManagement.service.FinalReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/pbl-classes/{pblClassId}/final-reports")
@RequiredArgsConstructor
public class FinalReportController {
    private final FinalReportService finalReportService;

    // Student: Create or update their group's final report
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<FinalReportResponseDTO> createOrUpdateReport(
            @PathVariable String pblClassId,
            @Valid @RequestPart("report") FinalReportRequestDTO dto,
            @RequestPart("file") MultipartFile file,
            @CurrentUser Account account) {

        FinalReportResponseDTO report = finalReportService.createOrUpdateReport(pblClassId, dto, file, account);
        return ResponseEntity.status(HttpStatus.CREATED).body(report);
    }

    // Student: Get their group's report
    @GetMapping("/my-group")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<FinalReportResponseDTO> getMyGroupReport(
            @PathVariable String pblClassId,
            @CurrentUser Account account) {

        FinalReportResponseDTO report = finalReportService.getMyGroupReport(pblClassId, account);
        return ResponseEntity.ok(report);
    }

    // Lecturer: Get all reports for this class
    @GetMapping
    @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')")
    public ResponseEntity<List<FinalReportSummaryDTO>> getAllReportsForClass(
            @PathVariable String pblClassId,
            @CurrentUser Account account) {

        List<FinalReportSummaryDTO> reports = finalReportService.getAllReportsForClass(pblClassId, account);
        return ResponseEntity.ok(reports);
    }

    // Lecturer Get specific report by ID
    @PreAuthorize("hasRole('LECTURER')")
    @GetMapping("/{reportId}")
    public ResponseEntity<FinalReportResponseDTO> getReportById(
            @PathVariable String pblClassId,
            @PathVariable Long reportId,
            @CurrentUser Account account) {

        FinalReportResponseDTO report = finalReportService.getReportById(reportId, pblClassId, account);
        return ResponseEntity.ok(report);
    }

    // All roles: Download report file
    @GetMapping("/{reportId}/download")
    public ResponseEntity<Resource> downloadReport(
            @PathVariable String pblClassId,
            @PathVariable Long reportId,
            @CurrentUser Account account) {

        FileDownloadDTO download = finalReportService.downloadReport(reportId, pblClassId, account);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + download.getOriginalFileName() + "\"")
                .body(download.getResource());
    }

    // Admin: Delete report with criteria check
    @DeleteMapping("/admin/reports/{reportId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adminDeleteReport(
            @PathVariable Long reportId,
            @CurrentUser Account account) {
        finalReportService.adminDeleteReport(reportId, account);
        return ResponseEntity.noContent().build();
    }

    // Student: Toggle public status of their report
    @PutMapping("/{reportId}/public-toggle")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> togglePublicStatus(
            @PathVariable String pblClassId,
            @PathVariable Long reportId,
            @CurrentUser Account account) {
        finalReportService.togglePublicStatus(reportId, pblClassId, account);
        return ResponseEntity.ok().build();
    }
}
