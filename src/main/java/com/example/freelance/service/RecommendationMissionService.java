package com.example.freelance.service;

import com.example.freelance.dto.MissionRecommandationDTO;
import com.example.freelance.model.Competence;
import com.example.freelance.model.Freelancer;
import com.example.freelance.model.Mission;
import com.example.freelance.model.MissionFreelance;
import com.example.freelance.repository.EvaluationRepository;
import com.example.freelance.repository.FreelancerRepository;
import com.example.freelance.repository.MissionFreelanceRepository;
import com.example.freelance.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationMissionService {


    private final FreelancerRepository freelancerRepository;

    private final MissionRepository missionRepository;

    private static final Integer MIN_SIMILAR = 3;


    public List<Mission> recommendMissionForFreelance(Long freelanceId) {
        log.info("Début de la recommandation des missions pour le freelance ID: {}", freelanceId);

        // 1. Récupération du freelance
        Freelancer targetFreelance = freelancerRepository.findById(freelanceId).orElseThrow(() -> {
            log.error("Freelance non trouvée avec ID: {}", freelanceId);
            return new IllegalArgumentException("Freelance non trouvée : " + freelanceId);
        });
        log.info("Freelance récupéré : {}", targetFreelance.getNom());

        // 2. Récupération des compétences avec noms
        Set<Competence> competences = targetFreelance.getCompetences();
        Set<Long> targetCompetenceIds = competences.stream()
                .map(Competence::getId)
                .collect(Collectors.toSet());

        String competenceNames = competences.stream()
                .map(Competence::getNom)
                .collect(Collectors.joining(", "));
        log.info("Compétences du freelance : {}", competenceNames);

        // 3. Récupération des missions similaires par ID
        List<Long> similarMissionIds = missionRepository.findSimilarMissionIdF(freelanceId, MIN_SIMILAR);
        log.info("{} missions correspondantes trouvées pour le freelance ID: {}", similarMissionIds.size(), freelanceId);

        // 4. Chargement des missions complètes
        List<Mission> missions = missionRepository.findAllById(similarMissionIds);

        // 5. Log détaillé des missions recommandées avec leurs compétences
        for (Mission mission : missions) {
            String missionCompetenceNames = mission.getCompetences().stream()
                    .map(Competence::getNom)
                    .collect(Collectors.joining(", "));
            log.info("Mission recommandée : {} | Compétences : {}", mission.getTitre(), missionCompetenceNames);
        }

        return missions;
    }
}


