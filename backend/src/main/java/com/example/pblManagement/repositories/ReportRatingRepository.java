package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.ReportRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRatingRepository extends JpaRepository<ReportRating, Long> {

    Optional<ReportRating> findByReportIdAndUserId(Long reportId, String userId);

}