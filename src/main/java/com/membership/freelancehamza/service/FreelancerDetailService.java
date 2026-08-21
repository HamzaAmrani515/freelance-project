package com.membership.freelancehamza.service;

import com.membership.freelancehamza.dto.FreelancerDetailDto;
import com.membership.freelancehamza.dto.FreelancerMissionHistoryDto;
import com.membership.freelancehamza.dto.FreelancerRankingDto;
import com.membership.freelancehamza.entity.DeliveryStatus;
import com.membership.freelancehamza.entity.Evaluation;
import com.membership.freelancehamza.repository.EvaluationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FreelancerDetailService {

    private final RankingService rankingService;
    private final EvaluationRepository evaluationRepository;

    public FreelancerDetailService(
            RankingService rankingService,
            EvaluationRepository evaluationRepository
    ) {
        this.rankingService = rankingService;
        this.evaluationRepository = evaluationRepository;
    }

    // Cette méthode permet de récupérer le détail complet d'un freelance comme score final  , disponibilité tendance etc
    @Transactional(readOnly = true)
    public FreelancerDetailDto getFreelancerDetails(Long freelancerId) {

        // On recupere d'abord les informations du freelancer depuis le ranking.

        FreelancerRankingDto ranking = rankingService.getRanking()
                .stream()
                .filter(freelancer -> freelancer.freelancerId().equals(freelancerId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Freelancer not found: " + freelancerId));

        // Ensuite, on récupère l'historique des évaluations du freelanceret les evaluation sont tries de plus recent au plus ancien et on linite de 30 pour evite d'envoyer rop d'info pour le front

        List<FreelancerMissionHistoryDto> history = evaluationRepository
                .findByFreelancer_IdOrderByCreatedAtDesc(freelancerId)
                .stream()
                .limit(30)
                .map(this::toHistoryDto)
                .toList();

        /*
         * On construit l'objet final envoyé au front.
         * Cet objet contient à la fois les données du ranking
         * et l'historique des évaluations.
         */
        return new FreelancerDetailDto(
                ranking.freelancerId(),
                ranking.fullName(),
                ranking.skill(),
                ranking.seniority(),
                ranking.availabilityStatus(),
                ranking.companyName(),
                ranking.yearsExperience(),
                ranking.finalScore(),
                ranking.recentEvaluationScore(),
                ranking.reliabilityScore(),
                ranking.trendScore(),
                ranking.experienceScore(),
                ranking.availabilityScore(),
                ranking.trendLabel(),
                ranking.missionsCount(),
                ranking.evaluationsCount(),
                history
        );
    }


    private FreelancerMissionHistoryDto toHistoryDto(Evaluation evaluation) {

        // On recalcule le score de cette évaluation sur 5
        double score = calculateEvaluationScore(evaluation);

        /*
         * Si le statut de livraison est null,
         * on considère par défaut que la mission est livrée à temps.
         */
        String deliveryStatus = evaluation.getDeliveryStatus() == null
                ? "ON_TIME"
                : evaluation.getDeliveryStatus().name();

        /*
         * On retourne l'historique d'une mission évaluée :
         * mission, statut de livraison, notes, score, feedback et date.
         */
        return new FreelancerMissionHistoryDto(
                evaluation.getMission().getId(),
                evaluation.getMission().getTitle(),
                deliveryStatus,
                evaluation.getNote(),
                evaluation.getTechnicalQuality(),
                evaluation.getCommunication(),
                evaluation.getAutonomy(),
                evaluation.getTestQuality(),
                round2(score),
                evaluation.getFeedback(),
                evaluation.getCreatedAt()
        );
    }

    // calculer score de l'evaluation
    private double calculateEvaluationScore(Evaluation evaluation) {
        double deliveryScore = calculateDeliveryScore(evaluation.getDeliveryStatus());

        return 0.30 * valueOrNote(evaluation.getTechnicalQuality(), evaluation.getNote())
                + 0.20 * valueOrNote(evaluation.getCommunication(), evaluation.getNote())
                + 0.15 * valueOrNote(evaluation.getAutonomy(), evaluation.getNote())
                + 0.15 * valueOrNote(evaluation.getTestQuality(), evaluation.getNote())
                + 0.20 * deliveryScore;
    }

 // satatut de livraison
    private double calculateDeliveryScore(DeliveryStatus status) {
        if (status == DeliveryStatus.LATE_DELIVERY) {
            return 2.5;
        }

        if (status == DeliveryStatus.CANCELLED) {
            return 0.0;
        }

        return 5.0;
    }

    // cette methode sert a eviter les pb dans les anciens donnees
    private double valueOrNote(Integer value, Integer note) {
        if (value != null) {
            return value;
        }

        if (note != null) {
            return note;
        }

        return 0;
    }

    // pour arrondir le score 2 chiffres apres la virgule
    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}