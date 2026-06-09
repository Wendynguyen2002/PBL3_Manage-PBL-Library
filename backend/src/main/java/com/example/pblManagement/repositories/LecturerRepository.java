package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.Lecturer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, String> {
    @Query("""
    SELECT l FROM Lecturer l
    WHERE (:search IS NULL OR
           LOWER(l.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(l.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(l.specialization) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(l.position) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(l.degree) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(l.department.name) LIKE LOWER(CONCAT('%', :search, '%')))
""")
    Page<Lecturer> searchLecturers(@Param("search") String search, Pageable pageable);

    Optional<Lecturer> findByEmail(String email);
}
