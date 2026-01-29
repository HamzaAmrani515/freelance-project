package com.example.freelance.model;

import com.example.freelance.model.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "freelancer_id")
    private Long freelancerId;

    @Column(name = "mission_id")
    private Long missionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private NotificationType type;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "sent_via_websocket")
    private Boolean sentViaWebsocket = false;

    @ManyToOne
    @JoinColumn(name = "freelancer_ref_id")
    private Freelancer freelancer;

    @ManyToOne
    @JoinColumn(name = "mission_ref_id")
    private Mission mission;

    public Notification(String message, Freelancer freelancer, Mission mission) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
        this.freelancer = freelancer;
        this.mission = mission;
        if (freelancer != null) {
            this.freelancerId = freelancer.getId();
        }
        if (mission != null) {
            this.missionId = mission.getId();
        }
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }
}
