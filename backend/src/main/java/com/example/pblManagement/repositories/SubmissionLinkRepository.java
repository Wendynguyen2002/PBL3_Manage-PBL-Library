package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.SubmissionLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionLinkRepository extends JpaRepository<SubmissionLink, Long> {
}
