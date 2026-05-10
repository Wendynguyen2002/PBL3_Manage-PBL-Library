package com.example.pblManagement.service;

import com.example.pblManagement.model.dto.finalreport.FinalReportRequestDTO;
import com.example.pblManagement.model.dto.finalreport.FinalReportResponseDTO;
import com.example.pblManagement.model.dto.finalreport.FinalReportSummaryDTO;
import com.example.pblManagement.model.entities.Account;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FinalReportService {
    FinalReportResponseDTO createOrUpdateReport(String pblClassId,
                                                FinalReportRequestDTO dto,
                                                MultipartFile file,
                                                Account account) throws Exception;

    FinalReportResponseDTO getMyGroupReport(String pblClassId, Account account);

    List<FinalReportSummaryDTO> getAllReportsForClass(String pblClassId, Account account);

    FinalReportResponseDTO getReportById(Long reportId, String pblClassId, Account account);

    Resource downloadReport(Long reportId, String pblClassId, Account account) throws Exception;

    void deleteReport(Long reportId, String pblClassId, Account account) throws Exception;

    void adminDeleteReport(Long reportId, Account account) throws Exception;
}
