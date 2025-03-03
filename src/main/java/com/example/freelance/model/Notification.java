package com.example.freelance.model;

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

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private Boolean isRead = false;


    @ManyToOne
    @JoinColumn(name = "freelancer_id")
    private Freelancer freelancer;


    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;

    public Notification(String message, Freelancer freelancer, Mission mission) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.freelancer = freelancer;
        this.mission = mission;
    }


}


