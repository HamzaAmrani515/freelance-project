package com.example.freelance.model;

import jakarta.persistence.*;

@Entity
@Table(name = "freelance")//nom de table de base
public class FreelanceModel {
    @Id//reconnaitre la colonne ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)//pour incrementer automatiquement
    private Long id;
    private  String nom;
    private String prenom;
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
