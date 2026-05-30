package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.PblGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PblGroupRepository extends JpaRepository<PblGroup, Long> {
    List<PblGroup> findByPblClassIdOrderByGroupName(String pblClassId);

    // Validate group exists in class
    Optional<PblGroup> findByIdAndPblClassId(Long groupId, String pblClassId);

    // Check if student is already in ANY group in this class
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Enrollment e " +
            "WHERE e.student.id = :studentId AND e.pblClass.id = :pblClassId AND e.pblGroup IS NOT NULL")
    boolean isStudentInAnyGroup(@Param("studentId") String studentId, @Param("pblClassId") String pblClassId);

    // Get the group a student belongs to in a specific class
    @Query("SELECT e.pblGroup FROM Enrollment e WHERE e.student.id = :studentId AND e.pblClass.id = :pblClassId AND e.pblGroup IS NOT NULL")
    Optional<PblGroup> findStudentGroupInClass(@Param("studentId") String studentId, @Param("pblClassId") String pblClassId);

    @Query("SELECT g.id, SIZE(g.enrollments), g.groupName FROM PblGroup g WHERE g.pblClass.id = :pblClassId")
    List<Object[]> findGroupIdsAndMemberCounts(@Param("pblClassId") String pblClassId);

    @Query("SELECT DISTINCT g FROM PblGroup g LEFT JOIN FETCH g.enrollments WHERE g.id = :groupId")
    Optional<PblGroup> findByIdWithEnrollments(@Param("groupId") Long groupId);
}
