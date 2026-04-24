package com.example.pblManagement.model.entities;
import com.example.pblManagement.model.entities.enums.MembershipRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "pbl_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PblGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String groupName;

    // Many groups in one class
    @ManyToOne
    @JoinColumn(name = "pbl_class_id")
    private PblClass pblClass;

    // 1 group can only choose one project
    @OneToOne
    @JoinColumn(name = "project_id", unique = true)
    private Project project;


    @OneToMany(mappedBy = "pblGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMembership> memberships = new ArrayList<>();

    // Helper to get actual students
    public List<Student> getStudents() {
        return memberships.stream()
                .map(GroupMembership::getStudent)
                .collect(Collectors.toList());
    }

    // Helper to count current members
    public int getCurrentMemberCount() {
        return memberships.size();
    }

    // Helper to check if group is full (vs class max)
    public boolean isFull() {
        return getCurrentMemberCount() >= pblClass.getMaxStudentsPerGroup();
    }

    // Helper to check if group meets minimum requirement
    public boolean meetsMinimum() {
        return getCurrentMemberCount() >= pblClass.getMinStudentsPerGroup();
    }

    // Add student with role
    public GroupMembership addStudent(Student student, MembershipRole role) {
        if (isFull()) {
            throw new IllegalStateException("Group is full");
        }
        // Check if student already in this class
        if (student.getGroupForClass(this.pblClass).isPresent()) {
            throw new IllegalStateException("Student already in a group for this class");
        }

        GroupMembership membership = GroupMembership.builder()
                .student(student)
                .pblGroup(this)
                .role(role)
                .build();
        memberships.add(membership);
        student.getGroupMemberships().add(membership);
        return membership;
    }

    // Remove student from group (lecturer only)
    public void removeStudent(Student student) {
        GroupMembership membership = memberships.stream()
                .filter(m -> m.getStudent().equals(student))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Student not in this group"));

        memberships.remove(membership);
        student.getGroupMemberships().remove(membership);
    }
}
