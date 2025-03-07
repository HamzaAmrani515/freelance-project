package com.example.freelance.model;


import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.service.listeners.MissionListener;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "missions")
@EntityListeners(MissionListener.class)
@Data
@NoArgsConstructor
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false, length = 2000)
    private String description;

    private Double budget;

    private String duree;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionStatut statut;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "freelancer_id")
    private Freelancer freelancer;

@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
        name = "mission_competences",
        joinColumns = @JoinColumn(name = "mission_id"),
        inverseJoinColumns = @JoinColumn(name = "competence_id")
)
private Set<Competence> competences = new HashSet<>();
}