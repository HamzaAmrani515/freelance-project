package com.example.freelance.model;

import com.example.freelance.model.enums.FreelancerStatus;
import com.example.freelance.model.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "freelancers")
@Data
@NoArgsConstructor
public class Freelancer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "freelancer")
    private Set<Mission> missions;

    @OneToMany(mappedBy = "freelancer")
    private Set<Competence> competences;

    private Double experience;

    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    private String profil;

    @Enumerated(EnumType.STRING)
    private FreelancerStatus status;

    @OneToMany(mappedBy = "freelancer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Notification> notifications = new HashSet<>();


}
