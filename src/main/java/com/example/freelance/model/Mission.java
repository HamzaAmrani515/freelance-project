package com.example.freelance.model;


import com.example.freelance.model.enums.MissionStatut;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "missions")
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

    // Relation avec Client (une mission a un seul client)
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    // Relation many-to-many avec Competence
    @ManyToMany
    @JoinTable(name = "mission_competences", joinColumns = @JoinColumn(name = "mission_id"), inverseJoinColumns = @JoinColumn(name = "competence_id"))
    private Set<Competence> competences = new HashSet<>();
}
