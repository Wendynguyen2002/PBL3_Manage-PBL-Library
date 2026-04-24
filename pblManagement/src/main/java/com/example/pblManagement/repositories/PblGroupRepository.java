package com.example.pblManagement.repositories;

import com.example.pblManagement.model.entities.PblGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PblGroupRepository extends JpaRepository<PblGroup, Long> {
}
