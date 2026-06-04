package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.finalreport.FileDownloadDTO;
import com.example.pblManagement.model.dto.finalreport.LibraryReportResponseDTO;
import com.example.pblManagement.model.dto.finalreport.LibrarySearchRequestDTO;
import com.example.pblManagement.model.entities.Account;
import org.springframework.data.domain.Page;

public interface LibraryService {
    Page<LibraryReportResponseDTO> searchReports(LibrarySearchRequestDTO searchRequest);

    LibraryReportResponseDTO getReportDetails(Long reportId);

    FileDownloadDTO downloadReport(Long reportId);

    void rateReport(Long reportId, Integer rating, Account account);

    Integer getUserRating(Long reportId, Account account);

    void togglePublicStatus(Long reportId, String pblClassId, Account account);
}
