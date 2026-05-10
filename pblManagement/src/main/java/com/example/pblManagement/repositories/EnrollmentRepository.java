package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentIdAndPblClassId(String studentId, String pblClassId);

    boolean existsByStudentIdAndPblClassId(String studentId, String pblClassId);

    void deleteByStudentIdAndPblClassId(String studentId, String pblClassId);
}
