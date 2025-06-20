package com.example.myjavafxapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Councilor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lastName;
    private String firstName;

    @ManyToOne
    @JoinColumn(name = "council_list_id")
    private CouncilList councilList;

    private boolean isTitular;

    // Constructors
    public Councilor() {
    }

    public Councilor(String lastName, String firstName, CouncilList councilList, boolean isTitular) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.councilList = councilList;
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

    public CouncilList getCouncilList() {
        return councilList;
    }

    public void setCouncilList(CouncilList councilList) {
        this.councilList = councilList;
    }

    public boolean isTitular() {
        return isTitular;
    }

    public void setTitular(boolean titular) {
        isTitular = titular;
    }
}
