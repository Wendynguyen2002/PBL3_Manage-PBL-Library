package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.finalreport.FileDownloadDTO;
import com.example.pblManagement.model.dto.finalreport.FinalReportRequestDTO;
import com.example.pblManagement.model.dto.finalreport.FinalReportResponseDTO;
import com.example.pblManagement.model.dto.finalreport.FinalReportSummaryDTO;
import com.example.pblManagement.model.entities.Account;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FinalReportService {
    FinalReportResponseDTO createOrUpdateReport(String pblClassId,
                                                FinalReportRequestDTO dto,
                                                MultipartFile file,
                                                Account account) ;

    FinalReportResponseDTO getMyGroupReport(String pblClassId, Account account);

    List<FinalReportSummaryDTO> getAllReportsForClass(String pblClassId, Account account);

    FinalReportResponseDTO getReportById(Long reportId, String pblClassId, Account account);

    FileDownloadDTO downloadReport(Long reportId, String pblClassId, Account account);

    void adminDeleteReport(Long reportId, Account account);

    void togglePublicStatus(Long reportId, String pblClassId, Account account);
}
