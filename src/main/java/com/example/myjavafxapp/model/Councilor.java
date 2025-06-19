package com.example.myjavafxapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Councilor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lastName;
    private String firstName;
    private String listName; // This might later be a relationship to CouncilList
    private boolean isTitular;

    // Constructors
    public Councilor() {
    }

    public Councilor(String lastName, String firstName, String listName, boolean isTitular) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.listName = listName;
        this.isTitular = isTitular;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public boolean isTitular() {
        return isTitular;
    }

    public void setTitular(boolean titular) {
        isTitular = titular;
    }
}
