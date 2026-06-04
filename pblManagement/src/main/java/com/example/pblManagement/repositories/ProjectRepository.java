package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Find all projects for a specific PBL class
    List<Project> findByPblClassId(String pblClassId);

    boolean existsByPblClassIdAndTitle(String pblClassId, String title);
}
