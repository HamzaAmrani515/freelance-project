package com.membership.freelancehamza.service;

import com.membership.freelancehamza.dto.CreateEvaluationRequest;
import com.membership.freelancehamza.dto.EvaluationResponse;
import com.membership.freelancehamza.entity.DeliveryStatus;
import com.membership.freelancehamza.entity.Evaluation;
import com.membership.freelancehamza.entity.Freelancer;
import com.membership.freelancehamza.entity.Mission;
import com.membership.freelancehamza.entity.MissionFreelance;
import com.membership.freelancehamza.entity.MissionStatus;
import com.membership.freelancehamza.repository.EvaluationRepository;
import com.membership.freelancehamza.repository.FreelancerRepository;
import com.membership.freelancehamza.repository.MissionFreelanceRepository;
import com.membership.freelancehamza.repository.MissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EvaluationService {

    private final MissionRepository missionRepository;
    private final MissionFreelanceRepository missionFreelanceRepository;
    private final EvaluationRepository evaluationRepository;
    private final FreelancerRepository freelancerRepository;

    public EvaluationService(
            MissionRepository missionRepository,
            MissionFreelanceRepository missionFreelanceRepository,
            EvaluationRepository evaluationRepository,
            FreelancerRepository freelancerRepository
    ) {
        this.missionRepository = missionRepository;
        this.missionFreelanceRepository = missionFreelanceRepository;
        this.evaluationRepository = evaluationRepository;
        this.freelancerRepository = freelancerRepository;
    }

    /*
     * Cette methode est appelee quand un client  va ajoute une evaluation.
     * on recupre la mission et on trouve le freelncer liee a cette mission
     * on calcule le score de cette nouvelle evaluation et apres on le souvgarde
     * puios on meet ajpur le score fonal du freelancer
     */
    @Transactional
    public EvaluationResponse createEvaluation(CreateEvaluationRequest request) {
        validateRequest(request);

        // ici on recupere la mission evalue par le freelancer
        Mission mission = missionRepository.findById(request.missionId())
                .orElseThrow(() -> new RuntimeException("Mission not found: " + request.missionId()));

        // ici on recupere le freelnacer liee a cette mission


        MissionFreelance missionFreelance = missionFreelanceRepository.findById(mission.getId())
                .orElseThrow(() -> new RuntimeException("No freelancer linked to mission: " + mission.getId()));

        Freelancer freelancer = missionFreelance.getFreelancer();

        // on callucl le score de m'evaluation final sur 5
        double newEvaluationScore = calculateEvaluationScore(request);

        // on cree l'objet evaluatipon pour le souvgarder dans BD
        Evaluation evaluation = new Evaluation();
        evaluation.setMission(mission);
        evaluation.setFreelancer(freelancer);
        evaluation.setNote(clampNote((int) Math.round(newEvaluationScore)));
        evaluation.setTechnicalQuality(request.technicalQuality());
        evaluation.setCommunication(request.communication());
        evaluation.setAutonomy(request.autonomy());
        evaluation.setTestQuality(request.testQuality());
        evaluation.setDeliveryStatus(request.deliveryStatus());
        evaluation.setFeedback(request.feedback());
        evaluation.setCreatedAt(LocalDateTime.now());

        // AVE IN THE bd
        evaluationRepository.save(evaluation);

        /*
         apres rajoute la nouvelle evaluation on rcupere les dernier 4 evaluation puisqui'il serat a recalculer le nouveau score
         */
        List<Evaluation> lastFourEvaluations =
                evaluationRepository.findTop4ByFreelancer_IdOrderByCreatedAtDescIdDesc(freelancer.getId());

        /*
         * Le score final est calcule sur 5 puis converti sur 100.
         * Exemple : 4.5 / 5 devient 90 / 100.
         */
        double finalScoreOnFive = calculateWeightedFinalScore(lastFourEvaluations);
        double finalScoreOnHundred = round2(finalScoreOnFive * 20);

        // ici on voit la tendence qui peut etre  : improving, stable ou declining
        String trendLabel = calculateTrendLabel(lastFourEvaluations);

        // Mise a jour du score final et de la tendance dans la table freelancers
        freelancer.setFinalScore(finalScoreOnHundred);
        freelancer.setTrendLabel(trendLabel);
        freelancerRepository.save(freelancer);

        /*
         * ici c'est la lalivraison est annule la mission est cancelled
         * Sinon, on considere que la mission/jalon est terminé apres l'éevaluation.
         */
        if (request.deliveryStatus() == DeliveryStatus.CANCELLED) {
            mission.setStatus(MissionStatus.CANCELLED);
        } else {
            mission.setStatus(MissionStatus.COMPLETED);
        }

        missionRepository.save(mission);

        // Réponse envoyee au front apres la creation de l'evaluation
        return new EvaluationResponse(
                mission.getId(),
                freelancer.getId(),
                evaluation.getNote(),
                round2(newEvaluationScore),
                finalScoreOnHundred,
                request.deliveryStatus().name(),
                trendLabel,
                "Evaluation ajoutee. Score final mis a jour."
        );
    }

    /*
     * Cette methode calcule le score final du freelancer
     * à partir de ses 4 dernières évaluations.
     *
     * La derniere evaluation compte plus que les anciennes :
     * - derniere evaluation : 40%
     * - deuxieme derniere : 30%
     * - troisieme derniere : 20%
     * - quatrieme derniere : 10%
     *
     * Si le freelancer a seulement 2 ou 3 evaluations,
     * on divise par la somme des poids utilisés.
     */
    private double calculateWeightedFinalScore(List<Evaluation> evaluations) {
        if (evaluations == null || evaluations.isEmpty()) {
            return 0.0;
        }

        double[] weights = {0.40, 0.30, 0.20, 0.10};

        double weightedSum = 0.0;
        double usedWeights = 0.0;

        for (int i = 0; i < evaluations.size() && i < 4; i++) {
            double evaluationScore = calculateEvaluationScore(evaluations.get(i));

            weightedSum += evaluationScore * weights[i];
            usedWeights += weights[i];
        }

        if (usedWeights == 0.0) {
            return 0.0;
        }

        return round2(weightedSum / usedWeights);
    }

    /*
     * Calcul du score d'une nouvelle évaluation.
     * Chaque critère a un poids :
     * - qualité technique : 30%
     * - communication : 20%
     * - autonomie : 15%
     * - qualité des tests : 15%
     * - livraison : 20%
     */
    private double calculateEvaluationScore(CreateEvaluationRequest request) {
        double deliveryScore = calculateDeliveryScore(request.deliveryStatus());

        return round2(
                0.30 * request.technicalQuality()
                        + 0.20 * request.communication()
                        + 0.15 * request.autonomy()
                        + 0.15 * request.testQuality()
                        + 0.20 * deliveryScore
        );
    }

    /*
     * Même calcul que la methode precedente,
     * mais ici on part d'une Evaluation deja existe dans la base
     */
    private double calculateEvaluationScore(Evaluation evaluation) {
        double deliveryScore = calculateDeliveryScore(evaluation.getDeliveryStatus());

        return round2(
                0.30 * valueOrNote(evaluation.getTechnicalQuality(), evaluation.getNote())
                        + 0.20 * valueOrNote(evaluation.getCommunication(), evaluation.getNote())
                        + 0.15 * valueOrNote(evaluation.getAutonomy(), evaluation.getNote())
                        + 0.15 * valueOrNote(evaluation.getTestQuality(), evaluation.getNote())
                        + 0.20 * deliveryScore
        );
    }

    /*
     * La livraison est transforméee en note sur 5 :
     * - ON_TIME = 5
     * - LATE_DELIVERY = 2.5
     * - CANCELLED = 0
     */
    private double calculateDeliveryScore(DeliveryStatus status) {
        if (status == DeliveryStatus.LATE_DELIVERY) {
            return 2.5;
        }

        if (status == DeliveryStatus.CANCELLED) {
            return 0.0;
        }

        return 5.0;
    }

    /*
     * Cette methode calcule la tendance du freelancer.
     * On compare la dernière évaluation avec l'évaluation précédente.
     *
     * Si la différence est supérieure à 0.2 : IMPROVING
     * Si la différence est inférieure à -0.2 : DECLINING
     * Sinon : STABLE
     */
    private String calculateTrendLabel(List<Evaluation> evaluations) {
        if (evaluations == null || evaluations.size() < 2) {
            return "STABLE";
        }

        double latestScore = calculateEvaluationScore(evaluations.get(0));
        double previousScore = calculateEvaluationScore(evaluations.get(1));

        double difference = latestScore - previousScore;

        if (difference > 0.2) {
            return "IMPROVING";
        }

        if (difference < -0.2) {
            return "DECLINING";
        }

        return "STABLE";
    }

    /*
     * Verification des données envoyées par le front.
     * On vérifie que la mission existe, que le statut de livraison existe,
     * et que toutes les notes sont entre 1 et 5.
     */
    private void validateRequest(CreateEvaluationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Evaluation request is required");
        }

        if (request.missionId() == null) {
            throw new IllegalArgumentException("Mission ID is required");
        }

        if (request.deliveryStatus() == null) {
            throw new IllegalArgumentException("Delivery status is required");
        }

        validateScore("technicalQuality", request.technicalQuality());
        validateScore("communication", request.communication());
        validateScore("autonomy", request.autonomy());
        validateScore("testQuality", request.testQuality());
    }

    // Vérifie qu'une note est bien renseignee et qu'elle est entre 1 et 5
    private void validateScore(String fieldName, Integer value) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        if (value < 1 || value > 5) {
            throw new IllegalArgumentException(fieldName + " must be between 1 and 5");
        }
    }

    /*
     * Cette methode est utilisée pour eviter les problemes avec les anciennes donnees.
     */
    private double valueOrNote(Integer value, Integer note) {
        if (value != null) {
            return value;
        }

        if (note != null) {
            return note;
        }

        return 0;
    }

    // Securite pour garder une note entre 1 et 5
    private int clampNote(int value) {
        return Math.max(1, Math.min(5, value));
    }

    // Arrondir un nombre a 2 chiffres après la virgule
    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}