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

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final MissionRepository missionRepository;
    private final FreelancerRepository freelancerRepository;
    private final EvaluationRepository evaluationRepository;

    public List<FreelancerRecommendationDTO> recommendFreelancersForMission(Long missionId) {
        /*==============
         1) Récup mission cible
         ===============*/
        // Récupérer la mission de la base de donnée en associé a missionId
        Mission targetMission = missionRepository.findById(missionId).orElseThrow(() -> new IllegalArgumentException("Mission non trouvée : " + missionId));
        // Extraire les compétances de la mission précédement récupérée
        Set<Long> targetCompetenceIds = targetMission.getCompetences().stream().map(Competence::getId).collect(Collectors.toSet());

        // 2) Missions "similaires" => On ne charge que leurs IDs (pas les entités complètes)
        List<Long> similarMissionIds = missionRepository.findSimilarMissionIds(targetCompetenceIds, missionId);
        if (similarMissionIds.isEmpty()) {
            // Pas de missions similaires => peu/pas de recommandations
            return Collections.emptyList();
        }

        // 3) Récupérer les évaluations seulement sur ces missions similaires
        List<Evaluation> evaluationsOnSimilar = evaluationRepository.findAllByMissionIds(similarMissionIds);
        if (evaluationsOnSimilar.isEmpty()) {
            // Aucune évaluation => On pourra toujours recommander en se basant sur
            // l'expérience / la correspondance de compétences, si vous le souhaitez.
            return Collections.emptyList();
        }

        // 4) Group by freelancerId
        Map<Long, List<Evaluation>> evalsByFreelancer = evaluationsOnSimilar.stream().collect(Collectors.groupingBy(e -> e.getFreelancer().getId()));

        // 5) On récupère la liste des freelancers (leurs IDs) depuis evalsByFreelancer
        Set<Long> freelancerIds = evalsByFreelancer.keySet();
        if (freelancerIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 6) Charger en une fois tous les freelancers + leurs compétences (EntityGraph)
        List<Freelancer> freelancers = freelancerRepository.findAllWithCompetencesByIdIn(freelancerIds);

        // Pour un accès plus rapide par ID, on peut stocker dans une map
        Map<Long, Freelancer> freelancerMap = freelancers.stream().collect(Collectors.toMap(Freelancer::getId, f -> f));

        // 7) Calcul du score
        List<FreelancerRecommendationDTO> recommendations = new ArrayList<>();

        for (Map.Entry<Long, List<Evaluation>> entry : evalsByFreelancer.entrySet()) {
            Long freelancerId = entry.getKey();
            List<Evaluation> evals = entry.getValue();

            Freelancer freelancer = freelancerMap.get(freelancerId);
            if (freelancer == null) {
                continue; // éventuellement, le freelancer n'existe plus
            }

            // a) Correspondance des compétences
            Set<Long> freelancerCompetences = freelancer.getCompetences().stream().map(Competence::getId).collect(Collectors.toSet());

            Set<Long> intersection = new HashSet<>(freelancerCompetences);
            intersection.retainAll(targetCompetenceIds);
            int commonCount = intersection.size();

            // b) Expérience
            double experience = Optional.ofNullable(freelancer.getExperience()).orElse(0.0);

            // c) Note moyenne sur missions similaires ( en utilisant la méthode average déjà prédéfinie)
            double avgRating = evals.stream().mapToDouble(e -> Optional.ofNullable(e.getNote()).orElse(0.0)).average().orElse(0.0);

            // scoring
            int score = (int) ((commonCount * 2.0) + experience + (avgRating * 2.0));

            recommendations.add(new FreelancerRecommendationDTO(freelancerId, freelancer.getNom(), freelancer.getPrenom(), score));
        }

        // Trier la liste des recommendation en fonction du score
        recommendations.sort(Comparator.comparingDouble(FreelancerRecommendationDTO::getScore).reversed());
        return recommendations;
    }
}
