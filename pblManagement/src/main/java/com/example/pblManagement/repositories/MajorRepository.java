package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.Major;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MajorRepository extends JpaRepository<Major, String> {
    @Query("""
    SELECT m from Major m
    WHERE (:search IS NULL OR
           LOWER(m.id) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%'))) OR 
           LOWER(m.department.name) LIKE LOWER(CONCAT('%', :search, '%')) 
""")
    Page<Major> searchMajors(String search, Pageable pageable);
}
