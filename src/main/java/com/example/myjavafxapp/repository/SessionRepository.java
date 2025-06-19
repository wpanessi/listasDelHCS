package com.example.myjavafxapp.repository;

import com.example.myjavafxapp.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    // Basic CRUD operations are inherited from JpaRepository
}
