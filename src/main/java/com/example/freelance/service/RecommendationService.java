package com.example.freelance.service;

import com.example.freelance.dto.FreelancerRecommendationDTO;
import com.example.freelance.model.Competence;
import com.example.freelance.model.Evaluation;
import com.example.freelance.model.Freelancer;
import com.example.freelance.model.Mission;
import com.example.freelance.repository.EvaluationRepository;
import com.example.freelance.repository.FreelancerRepository;
import com.example.freelance.repository.MissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * J'ai utiliser chatgpt
 * Service de recommandation permettant de proposer des freelances
 * en fonction d'une mission donnée.
 *
 * @author Assala Hamoudi
 */
@Service
@Slf4j
public class RecommendationService {

    @Autowired
    private MissionRepository missionRepository;
    @Autowired
    private FreelancerRepository freelancerRepository;
    @Autowired
    private EvaluationRepository evaluationRepository;

    private Integer MIN_SIMILAR = 3;

    public List<FreelancerRecommendationDTO> recommendFreelancersForMission(Long missionId) {
        log.info("Début de la recommandation des freelances pour la mission ID: {}", missionId);

        Mission targetMission = missionRepository.findById(missionId).orElseThrow(() -> {
            log.error("Mission non trouvée avec ID: {}", missionId);
            return new IllegalArgumentException("Mission non trouvée : " + missionId);
        });
        log.info("Mission récupérée : {}", targetMission.getTitre());


        //On extrait les compétences requises pour la mission sous forme d’ID (targetCompetenceIds)(stock les id des competence dans un set).
        Set<Long> targetCompetenceIds = targetMission.getCompetences().stream().map(Competence::getId).collect(Collectors.toSet());
        log.info("Compétences de la mission cible récupérées: {}", targetCompetenceIds);

       /* Set<Long> x = new HashSet<>();
        for (Competence competence : targetMission.getCompetences()) {
            x.add(competence.getId());
        }*/

        List<Long> similarMissionIds = missionRepository.findSimilarMissionIds(targetCompetenceIds, missionId, MIN_SIMILAR);
        log.info("{} missions similaires trouvées pour la mission ID: {}", similarMissionIds.size(), missionId);

        if (similarMissionIds.isEmpty()) {
            log.warn("Aucune mission similaire trouvée, arrêt du processus de recommandation.");
            return Collections.emptyList();
        }

        List<Evaluation> evaluationsOnSimilar = evaluationRepository.findAllByMissionIds(similarMissionIds);
        log.info("{} évaluations récupérées pour les missions similaires.", evaluationsOnSimilar.size());

        if (evaluationsOnSimilar.isEmpty()) {
            log.warn("Aucune évaluation trouvée, arrêt du processus de recommandation.");
            return Collections.emptyList();
        }

        Map<Long, List<Evaluation>> evalsByFreelancer = evaluationsOnSimilar.stream().collect(Collectors.groupingBy(e -> e.getFreelancer().getId()));
        log.info("Regroupement des évaluations par freelance effectué.");

        Set<Long> freelancerIds = evalsByFreelancer.keySet();
        if (freelancerIds.isEmpty()) {
            log.warn("Aucun freelance évalué, arrêt du processus de recommandation.");
            return Collections.emptyList();
        }

        List<Freelancer> freelancers = freelancerRepository.findAllWithCompetencesByIdIn(freelancerIds);
        log.info("{} freelances récupérés avec leurs compétences.", freelancers.size());
//map les freelance     avec leurs Id
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
}
