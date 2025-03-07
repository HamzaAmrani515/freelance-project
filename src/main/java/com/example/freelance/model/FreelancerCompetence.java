package com.example.freelance.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "freelancer_competences")
@Data
@NoArgsConstructor
public class FreelancerCompetence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "freelancer_id", nullable = false)
    private Freelancer freelancer;

    @ManyToOne
    @JoinColumn(name = "competence_id", nullable = false)
    private Competence competence;

    public FreelancerCompetence(Freelancer freelancer, Competence competence) {
        this.freelancer = freelancer;
        this.competence = competence;
    }
}
