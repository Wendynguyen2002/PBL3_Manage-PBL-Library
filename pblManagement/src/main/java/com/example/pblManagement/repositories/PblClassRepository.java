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

    // Find classes a student is enrolled in (via Enrollment)
    @Query("SELECT DISTINCT ce.pblClass FROM Enrollment ce WHERE ce.student.id = :studentId")
    List<PblClass> findByEnrolledStudentId(@Param("studentId") String studentId);

    // Check if a student is enrolled in a specific class
    @Query("SELECT COUNT(ce) > 0 FROM Enrollment ce WHERE ce.pblClass.id = :pblClassId AND ce.student.id = :studentId")
    boolean isStudentEnrolledInClass(@Param("pblClassId") String pblClassId, @Param("studentId") String studentId);

    // Get enrolled students for a specific class
    @Query("SELECT ce.student FROM Enrollment ce WHERE ce.pblClass.id = :pblClassId")
    List<Student> findEnrolledStudentsByPblClassId(@Param("pblClassId") String pblClassId);

    // Count enrolled students for a class
    @Query("SELECT COUNT(ce) FROM Enrollment ce WHERE ce.pblClass.id = :pblClassId")
    long countEnrolledStudentsByPblClassId(@Param("pblClassId") String pblClassId);

    // Find students that are eligible for a class based on majors AND not already enrolled
    @Query("SELECT s FROM Student s WHERE s.major.id IN :majorIds " +
            "AND s.id NOT IN (SELECT ce.student.id FROM Enrollment ce WHERE ce.pblClass.id = :pblClassId)")
    List<Student> findAvailableStudentsByMajors(
            @Param("pblClassId") String pblClassId,
            @Param("majorIds") List<String> majorIds
    );

    // Check if a student's major matches any of the class's allowed majors
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
            "FROM PblClass pc JOIN pc.majors m " +
            "WHERE pc.id = :pblClassId AND m.id = :majorId")
    boolean isMajorAllowedForClass(
            @Param("pblClassId") String pblClassId,
            @Param("majorId") String majorId
    );
}
