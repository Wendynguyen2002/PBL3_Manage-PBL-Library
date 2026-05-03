package com.example.pblManagement.repositories;

import com.example.pblManagement.model.dto.others.DepartmentSummaryDTO;
import com.example.pblManagement.model.entities.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {
    @Query("""
        SELECT d FROM Department d
        WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :search, '%'))
""")
    Page<Department> searchDepartments(String search, Pageable pageable);

    @Query("SELECT d FROM Department d ORDER BY d.name")
    List<DepartmentSummaryDTO> findAllForDropdown();

}
