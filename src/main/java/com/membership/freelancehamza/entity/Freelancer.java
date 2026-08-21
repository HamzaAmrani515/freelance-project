package com.membership.freelancehamza.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "freelancers")
public class Freelancer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "skill", length = 100)
    private String skill;

    @Column(name = "seniority", length = 50)
    private String seniority;

    @Column(name = "availability_status", length = 50)
    private String availabilityStatus;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(name = "company_name", length = 150)
    private String companyName;

    @Column(name = "final_score")
    private Double finalScore;

    @Column(name = "trend_label", length = 30)
    private String trendLabel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Freelancer() {
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (finalScore == null) {
            finalScore = 0.0;
        }

        if (trendLabel == null) {
            trendLabel = "STABLE";
        }
    }

    public Long getId() {
        return id;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNom() {
        return nom;
    }

    public String getSkill() {
        return skill;
    }

    public String getSeniority() {
        return seniority;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public Integer getYearsExperience() {
        return yearsExperience;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public String getTrendLabel() {
        return trendLabel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public void setSeniority(String seniority) {
        this.seniority = seniority;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public void setYearsExperience(Integer yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    public void setTrendLabel(String trendLabel) {
        this.trendLabel = trendLabel;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}