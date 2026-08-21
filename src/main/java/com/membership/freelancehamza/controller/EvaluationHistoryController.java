package com.membership.freelancehamza.controller;

import com.membership.freelancehamza.dto.FreelancerEvaluationHistoryDto;
import com.membership.freelancehamza.entity.DeliveryStatus;
import com.membership.freelancehamza.entity.Evaluation;
import com.membership.freelancehamza.repository.EvaluationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
@CrossOrigin
public class EvaluationHistoryController {

    private final EvaluationRepository evaluationRepository;

    public EvaluationHistoryController(EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }

    @GetMapping("/freelancer/{freelancerId}")
    public List<FreelancerEvaluationHistoryDto> getFreelancerEvaluations(
            @PathVariable Long freelancerId
    ) {
        return evaluationRepository.findByFreelancer_IdOrderByCreatedAtDesc(freelancerId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private FreelancerEvaluationHistoryDto toDto(Evaluation evaluation) {
        Long missionId = evaluation.getMission() == null
                ? null
                : evaluation.getMission().getId();

        String missionTitle = missionId == null
                ? "Mission non renseignée"
                : "Mission #" + missionId;

        double missionScore = calculateEvaluationScore(evaluation);

        return new FreelancerEvaluationHistoryDto(
                missionId,
                missionTitle,
                evaluation.getDeliveryStatus() == null
                        ? "ON_TIME"
                        : evaluation.getDeliveryStatus().name(),
                evaluation.getNote(),
                evaluation.getTechnicalQuality(),
                evaluation.getCommunication(),
                evaluation.getAutonomy(),
                evaluation.getTestQuality(),
                missionScore,
                evaluation.getFeedback(),
                evaluation.getCreatedAt()
        );
    }

    private double calculateEvaluationScore(Evaluation evaluation) {
        double deliveryScore = calculateDeliveryScore(evaluation.getDeliveryStatus());

        double technicalQuality = valueOrNote(evaluation.getTechnicalQuality(), evaluation.getNote());
        double communication = valueOrNote(evaluation.getCommunication(), evaluation.getNote());
        double autonomy = valueOrNote(evaluation.getAutonomy(), evaluation.getNote());
        double testQuality = valueOrNote(evaluation.getTestQuality(), evaluation.getNote());

        return round2(
                0.30 * technicalQuality
                        + 0.20 * communication
                        + 0.15 * autonomy
                        + 0.15 * testQuality
                        + 0.20 * deliveryScore
        );
    }

    private double calculateDeliveryScore(DeliveryStatus status) {
        if (status == DeliveryStatus.LATE_DELIVERY) return 2.5;
        if (status == DeliveryStatus.CANCELLED) return 0.0;
        return 5.0;
    }

    private double valueOrNote(Integer value, Integer note) {
        if (value != null) return value;
        if (note != null) return note;
        return 0.0;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}