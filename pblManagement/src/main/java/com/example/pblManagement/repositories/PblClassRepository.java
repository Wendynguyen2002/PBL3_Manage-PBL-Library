package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.PblClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PblClassRepository extends JpaRepository<PblClass, String> {
}
