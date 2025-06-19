package com.example.myjavafxapp.repository;

import com.example.myjavafxapp.model.CouncilList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouncilListRepository extends JpaRepository<CouncilList, Long> {
    // Basic CRUD operations are inherited from JpaRepository
    // Custom query methods can be added here if needed later
}
