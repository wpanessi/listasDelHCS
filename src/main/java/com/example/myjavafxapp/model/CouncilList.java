package com.example.myjavafxapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CouncilList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String listNumber; // Using String for flexibility
    private String cloister;

    // Constructors
    public CouncilList() {
    }

    public CouncilList(String listNumber, String cloister) {
        this.listNumber = listNumber;
        this.cloister = cloister;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getListNumber() {
        return listNumber;
    }

    public void setListNumber(String listNumber) {
        this.listNumber = listNumber;
    }

    public String getCloister() {
        return cloister;
    }

    public void setCloister(String cloister) {
        this.cloister = cloister;
    }

    @Override
    public String toString() {
        return (listNumber != null ? listNumber : "N/A") +
               (cloister != null && !cloister.isEmpty() ? " (" + cloister + ")" : "");
    }
}
