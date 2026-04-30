package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.PblClass;
import com.example.pblManagement.model.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PblClassRepository extends JpaRepository<PblClass, String> {
    // Find classes taught by a specific lecturer
    List<PblClass> findByLecturerId(String lecturerId);

    // Find classes a student is enrolled in
    @Query("SELECT DISTINCT pc FROM PblClass pc JOIN pc.enrolledStudents s WHERE s.id = :studentId")
    List<PblClass> findByEnrolledStudentId(@Param("studentId") String studentId);

    // Check if a student is enrolled in a specific class
    @Query("SELECT COUNT(pc) > 0 FROM PblClass pc JOIN pc.enrolledStudents s WHERE pc.id = :PblClassId AND s.id = :studentId")
    boolean isStudentEnrolledInClass(@Param("PblClassId") String PblClassId, @Param("studentId") String studentId);

    // Get enrolled students for a specific class (with pagination support)
    @Query("SELECT s FROM PblClass pc JOIN pc.enrolledStudents s WHERE pc.id = :PblClassId")
    List<Student> findEnrolledStudentsByPblClassId(@Param("PblClassId") String PblClassId);

    // Count enrolled students for a class
    @Query("SELECT COUNT(s) FROM PblClass pc JOIN pc.enrolledStudents s WHERE pc.id = :PblClassId")
    long countEnrolledStudentsByPblClassId(@Param("PblClassId") String PblClassId);
}
