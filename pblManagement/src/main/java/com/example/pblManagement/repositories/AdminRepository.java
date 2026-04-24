package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.Admin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, String> {
    boolean existsByEmail(@NotBlank(message = "Email is required") @Email(message = "Invalid email format") @Size(max = 100, message = "Email cannot exceed 100 characters") String email);

    @Query("""
    SELECT a FROM Admin a
    WHERE (:search IS NULL OR
           LOWER(a.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR
           LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    Page<Admin> searchAdmins(@Param("search") String search, Pageable pageable);
}
