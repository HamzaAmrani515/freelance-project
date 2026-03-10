package com.example.freelance.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "freelance_reports")
public class FreelancerReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "freelancer_id", nullable = true)
    private Freelancer freelancer;

    private Long joursTravailles;

    private Double montantVerse;

    private Double montantPayeClient;

    private Double profit;
}
