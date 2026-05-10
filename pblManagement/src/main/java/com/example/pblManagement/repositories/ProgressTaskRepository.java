package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.ProgressTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgressTaskRepository extends JpaRepository<ProgressTask, Long> {
    List<ProgressTask> findByPblClassIdOrderByDueDateAsc(String pblClassId);

    @Query("SELECT COUNT(t) > 0 FROM ProgressTask t WHERE t.pblClass.id = :pblClassId AND t.id = :taskId")
    boolean isTaskBelongsToClass(@Param("pblClassId") String pblClassId, @Param("taskId") Long taskId);
}
