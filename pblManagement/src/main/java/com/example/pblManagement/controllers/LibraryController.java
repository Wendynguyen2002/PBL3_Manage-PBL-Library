package com.example.pblManagement.controllers;

import com.example.pblManagement.model.dto.finalreport.LibraryReportResponseDTO;
import com.example.pblManagement.model.dto.finalreport.LibrarySearchRequestDTO;
import com.example.pblManagement.model.entities.Account;
import com.example.pblManagement.security.CurrentUser;
import com.example.pblManagement.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {
    private final LibraryService libraryService;

    // Public: Search library (no login required)
    @PostMapping("/search")
    public ResponseEntity<Page<LibraryReportResponseDTO>> searchReports(
            @RequestBody LibrarySearchRequestDTO searchRequest) {
        Page<LibraryReportResponseDTO> reports = libraryService.searchReports(searchRequest);
        return ResponseEntity.ok(reports);
    }

    // Public: Get report details (no login required)
    @GetMapping("/reports/{reportId}")
    public ResponseEntity<LibraryReportResponseDTO> getReportDetails(
            @PathVariable Long reportId) {
        LibraryReportResponseDTO report = libraryService.getReportDetails(reportId);
        return ResponseEntity.ok(report);
    }

    // Public: Download report (no login required)
    @GetMapping("/reports/{reportId}/download")
    public ResponseEntity<Resource> downloadReport(
            @PathVariable Long reportId) throws Exception {
        Resource resource = libraryService.downloadReport(reportId);
        LibraryReportResponseDTO report = libraryService.getReportDetails(reportId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + report.getTitle() + "." +
                                report.getFileType().toLowerCase() + "\"")
                .body(resource);
    }

    // Authenticated: Rate a report
    @PostMapping("/reports/{reportId}/rate")
    public ResponseEntity<Void> rateReport(
            @PathVariable Long reportId,
            @RequestParam Integer rating,
            @CurrentUser Account account) {
        libraryService.rateReport(reportId, rating, account);
        return ResponseEntity.ok().build();
    }

    // Authenticated: Get user's rating for a report
    @GetMapping("/reports/{reportId}/my-rating")
    public ResponseEntity<Integer> getUserRating(
            @PathVariable Long reportId,
            @CurrentUser Account account) {
        Integer rating = libraryService.getUserRating(reportId, account);
        return ResponseEntity.ok(rating);
    }

    // Student: Toggle public status of their own report
    @PutMapping("/pbl-classes/{pblClassId}/reports/{reportId}/public-toggle")
    public ResponseEntity<Void> togglePublicStatus(
            @PathVariable String pblClassId,
            @PathVariable Long reportId,
            @CurrentUser Account account) {
        libraryService.togglePublicStatus(reportId, pblClassId, account);
        return ResponseEntity.ok().build();
    }
}
