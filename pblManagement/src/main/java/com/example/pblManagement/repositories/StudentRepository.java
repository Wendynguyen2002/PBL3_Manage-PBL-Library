package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.Student;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    boolean existsByEmail(@NotBlank(message = "Email is required") @Email(message = "Invalid email format") @Size(max = 100, message = "Email cannot exceed 100 characters") String email);
    @Query("""
    SELECT s FROM Student s
    WHERE (:search IS NULL OR
           LOWER(s.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(s.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(s.homeClass) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(s.major.name) LIKE LOWER(CONCAT('%', :search, '%')))
""")
    Page<Student> searchStudents(@Param("search") String search, Pageable pageable);

    Optional<Student> findByEmail(String email);

}
