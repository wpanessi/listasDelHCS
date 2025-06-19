package com.example.myjavafxapp.repository;

import com.example.myjavafxapp.model.Councilor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouncilorRepository extends JpaRepository<Councilor, Long> {
    // Basic CRUD operations are inherited from JpaRepository
    // Custom query methods can be added here if needed later
}
