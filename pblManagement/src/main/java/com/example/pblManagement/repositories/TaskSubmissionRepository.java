package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.TaskSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission, String> {
    // Find submission by task and group (1 submission per group per task)
    Optional<TaskSubmission> findByTaskIdAndGroupId(Long taskId, Long groupId);

    // Check if group has already submitted for a task
    boolean existsByTaskIdAndGroupId(Long taskId, Long groupId);

    // Find all submissions for a specific task (for lecturer view)
    List<TaskSubmission> findByTaskIdOrderBySubmittedAtDesc(Long taskId);

    // Find all submissions by a specific group (across all tasks)
    List<TaskSubmission> findByGroupId(Long groupId);
}
