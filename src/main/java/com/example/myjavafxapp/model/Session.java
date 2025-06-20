package com.example.myjavafxapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionNumberPerYear;
    private LocalDate date;
    private boolean isActive = false; // Default to false

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "session_attendance",
        joinColumns = @JoinColumn(name = "session_id"),
        inverseJoinColumns = @JoinColumn(name = "councilor_id")
    )
    private Set<Councilor> presentCouncilors = new HashSet<>();

    // Constructors
    public Session() {
    }

    public Session(String sessionNumberPerYear, LocalDate date) {
        this.sessionNumberPerYear = sessionNumberPerYear;
        this.date = date;
        this.isActive = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionNumberPerYear() {
        return sessionNumberPerYear;
    }

    public void setSessionNumberPerYear(String sessionNumberPerYear) {
        this.sessionNumberPerYear = sessionNumberPerYear;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Set<Councilor> getPresentCouncilors() {
        return presentCouncilors;
    }

    public void setPresentCouncilors(Set<Councilor> presentCouncilors) {
        this.presentCouncilors = presentCouncilors;
    }
}
