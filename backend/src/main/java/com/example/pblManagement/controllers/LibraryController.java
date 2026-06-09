package com.example.pblManagement.controllers;

import com.example.pblManagement.model.dto.finalreport.FileDownloadDTO;
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

    // All roles: Search the library
    @PostMapping("/search")
    public ResponseEntity<Page<LibraryReportResponseDTO>> searchReports(
            @RequestBody LibrarySearchRequestDTO searchRequest) {
        Page<LibraryReportResponseDTO> reports = libraryService.searchReports(searchRequest);
        return ResponseEntity.ok(reports);
    }

    // All roles: Get report details
    @GetMapping("/reports/{reportId}")
    public ResponseEntity<LibraryReportResponseDTO> getReportDetails(
            @PathVariable Long reportId) {
        LibraryReportResponseDTO report = libraryService.getReportDetails(reportId);
        return ResponseEntity.ok(report);
    }

    // All roles: Download report file
    @GetMapping("/reports/{reportId}/download")
    public ResponseEntity<Resource> downloadReport(@PathVariable Long reportId) {
        FileDownloadDTO download = libraryService.downloadReport(reportId);

        // Build filename: title + extension
        String extension = download.getFileType().toLowerCase();
        String filename = download.getTitle().replaceAll("\\s+", "_") + "." + extension;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .body(download.getResource());
    }

    // All roles: Rate a report
    @PostMapping("/reports/{reportId}/rate")
    public ResponseEntity<Void> rateReport(
            @PathVariable Long reportId,
            @RequestParam Integer rating,
            @CurrentUser Account account) {
        libraryService.rateReport(reportId, rating, account);
        return ResponseEntity.ok().build();
    }

    // All roles: See ratings of a report in the library
    @GetMapping("/reports/{reportId}/my-rating")
    public ResponseEntity<Integer> getUserRating(
            @PathVariable Long reportId,
            @CurrentUser Account account) {
        Integer rating = libraryService.getUserRating(reportId, account);
        return ResponseEntity.ok(rating);
    }
}
