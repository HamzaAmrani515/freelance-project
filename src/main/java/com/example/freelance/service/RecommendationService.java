package com.example.freelance.service;

import com.example.freelance.dto.FreelancerRecommendationDTO;
import com.example.freelance.model.Competence;
import com.example.freelance.model.Evaluation;
import com.example.freelance.model.Freelancer;
import com.example.freelance.model.Mission;
import com.example.freelance.repository.EvaluationRepository;
import com.example.freelance.repository.FreelancerRepository;
import com.example.freelance.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de recommandation permettant de proposer des freelances
 * en fonction d'une mission donnée.
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    /**
     * Repository pour accéder aux données des missions
     */
    private final MissionRepository missionRepository;
    /**
     * Repository pour accéder aux données des freelances
     */
    private final FreelancerRepository freelancerRepository;
    /**
     * Repository pour accéder aux données des évaluations
     */
    private final EvaluationRepository evaluationRepository;

    /**
     * Constante définissant le nombre minimal de compétences communes.
     */
    private static final Integer MIN_SIMILAR = 2;

    /**
     * Recommande une liste de freelances pour une mission précise.
     *
     * @param missionId l'identifiant de la mission cible
     * @return une liste d'objets {@link FreelancerRecommendationDTO} contenant le freelance et son score
     */
    public List<FreelancerRecommendationDTO> recommendFreelancersForMission(Long missionId) {

        /*==============
         1) Récupération de la mission cible
         ===============*/

        // Récupère la mission en base à partir de l'id fourni, ou lance une exception si absente
        Mission targetMission = missionRepository.findById(missionId).orElseThrow(() -> new IllegalArgumentException("Mission non trouvée : " + missionId));

        // Extrait l'ensemble des ID de compétences associées à la mission cible
        Set<Long> targetCompetenceIds = targetMission.getCompetences().stream().map(Competence::getId).collect(Collectors.toSet());

        /*==============
         2) Rechercher les missions "similaires"
         ===============*/

        // Récupère les IDs des missions dont les compétences sont suffisamment proches
        // (au moins MIN_SIMILAR compétences en commun) de celles de la mission cible
        List<Long> similarMissionIds = missionRepository.findSimilarMissionIds(targetCompetenceIds, missionId, MIN_SIMILAR);

        // Si aucune mission n'est similaire, on retourne une liste vide (pas de recommandation possible)
        if (similarMissionIds.isEmpty()) {
            return Collections.emptyList();
        }

        /*==============
         3) Charger toutes les évaluations des missions similaires
         ===============*/

        // Recherche en base toutes les évaluations associées aux missions similaires
        List<Evaluation> evaluationsOnSimilar = evaluationRepository.findAllByMissionIds(similarMissionIds);

        // Si on ne trouve aucune évaluation, on renvoie une liste vide
        // (aucune donnée sur laquelle baser un score de recommandation)
        if (evaluationsOnSimilar.isEmpty()) {
            return Collections.emptyList();
        }

        /*==============
         4) Regrouper les évaluations par freelance
         ===============*/

        // Transforme la liste d'évaluations en une map : {freelancerId -> liste d'Evaluation}
        Map<Long, List<Evaluation>> evalsByFreelancer = evaluationsOnSimilar.stream().collect(Collectors.groupingBy(e -> e.getFreelancer().getId()));

        /*==============
         5) Récupérer la liste des freelances concernés (uniquement ceux qui ont des évaluations)
         ===============*/

        // On extrait l'ensemble des ID de freelances depuis la map
        Set<Long> freelancerIds = evalsByFreelancer.keySet();
        if (freelancerIds.isEmpty()) {
            return Collections.emptyList();
        }

        /*==============
         6) Charger en une fois tous les freelances + leurs compétences
         ===============*/

        // Récupère en base la liste des freelances concernés (avec leurs compétences) grâce à un EntityGraph
        List<Freelancer> freelancers = freelancerRepository.findAllWithCompetencesByIdIn(freelancerIds);

        // Crée une map {freelancerId -> Freelancer} pour un accès plus rapide lors du calcul
        Map<Long, Freelancer> freelancerMap = freelancers.stream().collect(Collectors.toMap(Freelancer::getId, f -> f));

        /*==============
         7) Calcul du score pour chaque freelance
         ===============*/

        // Liste qui contiendra les résultats (DTOs) de recommandation
        List<FreelancerRecommendationDTO> recommendations = new ArrayList<>();

        // On parcourt chaque freelance et ses évaluations
        for (Map.Entry<Long, List<Evaluation>> entry : evalsByFreelancer.entrySet()) {
            Long freelancerId = entry.getKey();
            List<Evaluation> evals = entry.getValue();

            // Récupération du freelance complet depuis la map
            Freelancer freelancer = freelancerMap.get(freelancerId);
            if (freelancer == null) {
                // Si le freelance n'existe plus ou a été supprimé, on ignore
                continue;
            }

            // a) Correspondance des compétences entre le freelance et la mission cible
            Set<Long> freelancerCompetences = freelancer.getCompetences().stream().map(Competence::getId).collect(Collectors.toSet());

            // Intersection entre les compétences du freelance et celles de la mission
            Set<Long> intersection = new HashSet<>(freelancerCompetences);
            intersection.retainAll(targetCompetenceIds);
            int commonCount = intersection.size(); // nombre de compétences communes

            // Vérifie si le freelance a au moins MIN_SIMILAR compétences en commun
            if (commonCount >= MIN_SIMILAR) {
                // b) Expérience du freelance
                double experience = Optional.ofNullable(freelancer.getExperience()).orElse(0.0);

                // c) Note moyenne sur les missions similaires
                // On calcule la moyenne à partir des évaluations (si la note est null, on considère 0.0)
                double avgRating = evals.stream().mapToDouble(e -> Optional.ofNullable(e.getNote()).orElse(0.0)).average().orElse(0.0);

                // d) Calcul du score global :
                // - On double l'importance du nombre de compétences communes
                // - On ajoute l'expérience
                // - On double la note moyenne
                int score = (int) ((commonCount * 2.0) + experience + (avgRating * 2.0));

                // On ajoute la recommandation avec le score calculé
                recommendations.add(new FreelancerRecommendationDTO(freelancerId, freelancer.getNom(), freelancer.getPrenom(), score));
            }
        }

        // Trie les freelances recommandés par score décroissant
        recommendations.sort(Comparator.comparingDouble(FreelancerRecommendationDTO::getScore).reversed());

        // Retourne la liste finale de recommandation
        return recommendations;
    }
}
