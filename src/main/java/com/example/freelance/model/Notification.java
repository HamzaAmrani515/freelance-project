package com.example.freelance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String message;

    private LocalDateTime timestamp;


    private boolean isRead;

    @ManyToOne
    @JoinColumn(name = "freelancer_id")
    @JsonIgnoreProperties({"notifications", "missions"})
    private Freelancer freelancer;

    @ManyToOne
    @JoinColumn(name = "mission_id")
    @JsonIgnoreProperties({"freelancer", "client", "competences"})
    private Mission mission;

    public Notification(String message, Freelancer freelancer, Mission mission) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
        this.freelancer = freelancer;
        this.mission = mission;
    }
}
