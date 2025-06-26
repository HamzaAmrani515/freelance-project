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
        log.info("Début de la recommandation des mission pour les freelance ID: {}", freelanceId);

        Freelancer targetFreelance = freelancerRepository.findById(freelanceId).orElseThrow(() -> {
            log.error("Mission non trouvée avec ID: {}", freelanceId);
            return new IllegalArgumentException("Freelance non trouvée : " + freelanceId);
        });
        log.info("Mission récupérée : {}", targetFreelance.getNom());


        //On extrait les compétences requises pour la mission sous forme d’ID (targetCompetenceIds)(stock les id des competence dans un set).
        Set<Long> targetCompetenceIds = targetFreelance.getCompetences().stream().map(Competence::getId).collect(Collectors.toSet());
        log.info("Compétences du freelance cible récupérées: {}", targetCompetenceIds);

       /* Set<Long> x = new HashSet<>();
        for (Competence competence : targetMission.getCompetences()) {
            x.add(competence.getId());
        }*/

        List<Long> similarCompetance = missionRepository.findSimilarMissionIdF(freelanceId, MIN_SIMILAR);
        log.info("{} missions corespondante  trouvées pour le freeelance ID: {}", similarCompetance.size(), freelanceId);

         return missionRepository.findAllById(similarCompetance);
    }
}
