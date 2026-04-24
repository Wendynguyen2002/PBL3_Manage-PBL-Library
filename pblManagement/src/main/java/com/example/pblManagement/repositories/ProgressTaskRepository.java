package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.ProgressTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgressTaskRepository extends JpaRepository<ProgressTask, Long> {
}
