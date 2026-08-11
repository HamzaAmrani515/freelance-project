package com.membership.freelancehamza.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mission_events")
public class MissionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="mission_id", nullable = false)
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="freelancer_id", nullable = false)
    private Freelancer freelancer;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false, length = 30)
    private EventType type;

    @Column(name="description")
    private String description;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    public MissionEvent() {}

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Mission getMission() { return mission; }
    public Freelancer getFreelancer() { return freelancer; }
    public EventType getType() { return type; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setMission(Mission mission) { this.mission = mission; }
    public void setFreelancer(Freelancer freelancer) { this.freelancer = freelancer; }
    public void setType(EventType type) { this.type = type; }
    public void setDescription(String description) { this.description = description; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
