package com.example.myjavafxapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionNumberPerYear;
    private LocalDate date;

    // Constructors
    public Session() {
    }

    public Session(String sessionNumberPerYear, LocalDate date) {
        this.sessionNumberPerYear = sessionNumberPerYear;
        this.date = date;
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
}
