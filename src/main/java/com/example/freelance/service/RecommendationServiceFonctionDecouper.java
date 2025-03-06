package com.example.freelance.service;

import com.example.freelance.dto.FreelancerRecommendationDTO;
import com.example.freelance.model.Competence;
import com.example.freelance.model.Evaluation;
import com.example.freelance.model.Freelancer;
import com.example.freelance.model.Mission;
import com.example.freelance.repository.EvaluationRepository;
import com.example.freelance.repository.FreelancerRepository;
import com.example.freelance.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RecommendationServiceFonctionDecouper {
    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private FreelancerRepository freelancerRepository;
    @Autowired
    private EvaluationRepository evaluationRepository;
    private static final Integer MIN_SIMILAR = 2;


    public List<FreelancerRecommendationDTO> recommendFreelancersForMission(Long missionId) {
        log.info("Début de la recommandation des freelances pour la mission ID: {}", missionId);

        // 1. Récupération de la mission
        Mission targetMission = getMissionById(missionId);

        Set<Long> targetCompetenceIds = getTargetCompetenceIds(targetMission);
        log.info("Compétences de la mission cible récupérées: {}", targetCompetenceIds);

        List<Long> similarMissionIds = getSimilarMissionIds(targetCompetenceIds, missionId);

        log.info("{} missions similaires trouvées pour la mission ID: {}", similarMissionIds.size(), missionId);

        if (similarMissionIds.isEmpty()) {
            log.warn("Aucune mission similaire trouvée, arrêt du processus de recommandation.");
            return Collections.emptyList();
        }

        List<Evaluation> evaluationsOnSimilar = getEvaluationsOnSimilar(similarMissionIds);
        log.info("{} évaluations récupérées pour les missions similaires.", evaluationsOnSimilar.size());

        if (evaluationsOnSimilar.isEmpty()) {
            log.warn("Aucune évaluation trouvée, arrêt du processus de recommandation.");
            return Collections.emptyList();
        }
        Map<Long, List<Evaluation>> evalsByFreelancer = getEvalsByFreelancer(evaluationsOnSimilar);
        log.info("Regroupement des évaluations par freelance effectué.");

        Set<Long> freelancerIds = evalsByFreelancer.keySet();
        if (freelancerIds.isEmpty()) {
            log.warn("Aucun freelance évalué, arrêt du processus de recommandation.");
            return Collections.emptyList();
        }
        List<Freelancer> freelancers= getFreelancers(freelancerIds);
        log.info("{} freelances récupérés avec leurs compétences.", freelancers.size());
        Map<Long, Freelancer> freelancerMap = freelancers.stream().collect(Collectors.toMap(Freelancer::getId, f -> f));

        List<FreelancerRecommendationDTO> recommendations = new ArrayList<>();
        //boucler sur la map des evaluation regrouper par freelance
        for (Map.Entry<Long, List<Evaluation>> entry : evalsByFreelancer.entrySet()) {
            Long freelancerId = entry.getKey();
            List<Evaluation> evals = entry.getValue();
            Freelancer freelancer = freelancerMap.get(freelancerId);

            if (freelancer == null) {
                log.warn("Freelance avec ID {} introuvable, passage au suivant.", freelancerId);
                continue;
            }

            // Extraire la liste des compétences d'un freelance
            Set<Long> freelancerCompetences = freelancer.getCompetences().stream().map(Competence::getId).collect(Collectors.toSet());
            // Trouver le nombre de compétences communes entre celles du freelance et celles de la missions en entrée
            int commonCount = (int) freelancerCompetences.stream().filter(targetCompetenceIds::contains).count();
            log.info("Le freelancer id {} possède {} competence similaire", freelancer.getId(), commonCount);

            if (commonCount >= MIN_SIMILAR) {
                // je récupere l'expériance du freelance
                double experience = Optional.ofNullable(freelancer.getExperience()).orElse(0.0);
                // Je boucle sur la liste des évaluations et je calcul la moyenne des ces notes
                double avgRating = evals.stream().mapToDouble(e -> Optional.ofNullable(e.getNote()).orElse(0.0)).average().orElse(0.0);
                // Je calcul son score final avec un system de pondération
                int score = (int) ((commonCount * 2.0) + experience + (avgRating * 2.0));

                log.info("Freelancer {} - Score calculé: {}", freelancer.getNom(), score);
                // Dto c'est le format de retoure de mon API
                recommendations.add(new FreelancerRecommendationDTO(freelancerId, freelancer.getNom(), freelancer.getPrenom(), score));
            }
        }
        // Je tri la liste de recommendation
        recommendations.sort(Comparator.comparingDouble(FreelancerRecommendationDTO::getScore).reversed());
        log.info("Recommandation terminée. {} freelances recommandés avec les id {} .", recommendations.size(), recommendations.stream().map(FreelancerRecommendationDTO::getFreelancerId).toList());

        return recommendations;
    }

    private Mission getMissionById(Long missionId) {
        return missionRepository.findById(missionId).orElseThrow(() -> {
            log.error("Mission non trouvée avec ID: {}", missionId);
            return new IllegalArgumentException("Mission non trouvée : " + missionId);
        });
    }

    private Set<Long> getTargetCompetenceIds(Mission targetMission) {
        return targetMission.getCompetences().stream().map(Competence::getId).collect(Collectors.toSet());
    }

    private List<Long> getSimilarMissionIds(Set<Long> targetCompetenceIds, Long missionId){
        return missionRepository.findSimilarMissionIds(targetCompetenceIds, missionId, MIN_SIMILAR);
    }

    private  List<Evaluation> getEvaluationsOnSimilar(List<Long> similarMissionIds){
        return evaluationRepository.findAllByMissionIds(similarMissionIds);
    }

    private Map<Long, List<Evaluation>> getEvalsByFreelancer(List<Evaluation> evaluationsOnSimilar ){
        return evaluationsOnSimilar.stream().collect(Collectors.groupingBy(e -> e.getFreelancer().getId()));
    }

    private List<Freelancer> getFreelancers(Set<Long> freelancerIds){
        return freelancerRepository.findAllWithCompetencesByIdIn(freelancerIds);
    }


}


