package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.Project;
import com.example.pblManagement.model.entities.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Find all projects for a specific PBL class
    List<Project> findByPblClassId(String pblClassId);

    // Find all available projects for a specific PBL class (not taken yet)
    List<Project> findByPblClassIdAndStatus(String pblClassId, ProjectStatus status);

    // Find available projects for a class (for dropdown)
    @Query("SELECT p FROM Project p WHERE p.pblClass.id = :pblClassId AND (p.status = 'AVAILABLE' OR p.assignedGroup IS NULL)")
    List<Project> findAvailableProjectsByClassId(@Param("pblClassId") String pblClassId);

    // Check if a project is already taken
    @Query("SELECT COUNT(p) > 0 FROM Project p WHERE p.id = :projectId AND (p.status = 'TAKEN' OR p.assignedGroup IS NOT NULL)")
    boolean isProjectTaken(@Param("projectId") Long projectId);

    // Find project by ID with eager loading of assigned group (optional, for performance)
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.assignedGroup WHERE p.id = :projectId")
    Optional<Project> findByIdWithGroup(@Param("projectId") Long projectId);

    // Count available projects for a class
    long countByPblClassIdAndStatus(String pblClassId, ProjectStatus status);
}
