package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.FinalReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinalReportRepository extends JpaRepository<FinalReport, Long> {
    // Find report by group and class (1 per group per class)
    Optional<FinalReport> findByGroupIdAndPblClassId(Long groupId, String pblClassId);

    // Get all reports for a class (lecturer view)
    List<FinalReport> findByPblClassIdOrderBySubmittedAtDesc(String pblClassId);

    // Get report by ID and verify it belongs to class (security)
    Optional<FinalReport> findByIdAndPblClassId(Long id, String pblClassId);

    @Query("SELECT r FROM FinalReport r " +
            "WHERE r.isPublic = true " +
            "AND (:keyword IS NULL OR :keyword = '' OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "     OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:className IS NULL OR :className = '' OR LOWER(r.pblClass.className) LIKE LOWER(CONCAT('%', :className, '%'))) " +
            "AND (:departmentId IS NULL OR :departmentId = '' OR r.pblClass.lecturer.department.id = :departmentId) " +
            "AND (:fileType IS NULL OR :fileType = '' OR r.fileType = :fileType)")
    Page<FinalReport> findPublicReports(@Param("keyword") String keyword,
                                        @Param("className") String className,
                                        @Param("departmentId") String departmentId,
                                        @Param("fileType") String fileType,
                                        Pageable pageable);
}
