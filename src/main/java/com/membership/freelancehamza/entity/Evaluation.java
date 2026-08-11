package com.membership.freelancehamza.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluations")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="mission_id", nullable = false)
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="freelancer_id", nullable = false)
    private Freelancer freelancer;

    @Column(name="note", nullable = false)
    private Integer note;

    @Column(name="technical_quality")
    private Integer technicalQuality;

    @Column(name="communication")
    private Integer communication;

    @Column(name="deadline_respect")
    private Integer deadlineRespect;

    @Column(name="autonomy")
    private Integer autonomy;

    @Column(name="test_quality")
    private Integer testQuality;

    @Column(name="feedback")
    private String feedback;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    public Evaluation() {}

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Mission getMission() { return mission; }
    public Freelancer getFreelancer() { return freelancer; }
    public Integer getNote() { return note; }
    public Integer getTechnicalQuality() { return technicalQuality; }
    public Integer getCommunication() { return communication; }
    public Integer getDeadlineRespect() { return deadlineRespect; }
    public Integer getAutonomy() { return autonomy; }
    public Integer getTestQuality() { return testQuality; }
    public String getFeedback() { return feedback; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setMission(Mission mission) { this.mission = mission; }
    public void setFreelancer(Freelancer freelancer) { this.freelancer = freelancer; }
    public void setNote(Integer note) { this.note = note; }
    public void setTechnicalQuality(Integer technicalQuality) { this.technicalQuality = technicalQuality; }
    public void setCommunication(Integer communication) { this.communication = communication; }
    public void setDeadlineRespect(Integer deadlineRespect) { this.deadlineRespect = deadlineRespect; }
    public void setAutonomy(Integer autonomy) { this.autonomy = autonomy; }
    public void setTestQuality(Integer testQuality) { this.testQuality = testQuality; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}