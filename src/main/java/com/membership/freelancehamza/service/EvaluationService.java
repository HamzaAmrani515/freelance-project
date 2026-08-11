package com.membership.freelancehamza.service;

import com.membership.freelancehamza.dto.CreateEvaluationRequest;
import com.membership.freelancehamza.dto.EvaluationResponse;
import com.membership.freelancehamza.entity.Evaluation;
import com.membership.freelancehamza.entity.EventType;
import com.membership.freelancehamza.entity.Freelancer;
import com.membership.freelancehamza.entity.Mission;
import com.membership.freelancehamza.entity.MissionEvent;
import com.membership.freelancehamza.entity.MissionFreelance;
import com.membership.freelancehamza.repository.EvaluationRepository;
import com.membership.freelancehamza.repository.MissionEventRepository;
import com.membership.freelancehamza.repository.MissionFreelanceRepository;
import com.membership.freelancehamza.repository.MissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class EvaluationService {

    private final MissionRepository missionRepository;
    private final MissionFreelanceRepository missionFreelanceRepository;
    private final EvaluationRepository evaluationRepository;
    private final MissionEventRepository missionEventRepository;

    public EvaluationService(
            MissionRepository missionRepository,
            MissionFreelanceRepository missionFreelanceRepository,
            EvaluationRepository evaluationRepository,
            MissionEventRepository missionEventRepository
    ) {
        this.missionRepository = missionRepository;
        this.missionFreelanceRepository = missionFreelanceRepository;
        this.evaluationRepository = evaluationRepository;
        this.missionEventRepository = missionEventRepository;
    }

    @Transactional
    public EvaluationResponse createEvaluation(CreateEvaluationRequest request) {

        validateRequest(request);

        Mission mission = missionRepository.findById(request.missionId())
                .orElseThrow(() -> new RuntimeException("Mission not found: " + request.missionId()));

        MissionFreelance missionFreelance = missionFreelanceRepository.findById(mission.getId())
                .orElseThrow(() -> new RuntimeException("No freelancer linked to mission: " + mission.getId()));

        Freelancer freelancer = missionFreelance.getFreelancer();

        double evaluationScore = calculateEvaluationScore(request);
        int globalNote = clampNote((int) Math.round(evaluationScore));

        EventType generatedEventType = generateEventType(evaluationScore, request.deadlineRespect());
        String businessFeedback = buildBusinessFeedback(generatedEventType);

        LocalDateTime evaluatedAt = LocalDateTime.now();

        Evaluation evaluation = new Evaluation();
        evaluation.setMission(mission);
        evaluation.setFreelancer(freelancer);
        evaluation.setNote(globalNote);
        evaluation.setTechnicalQuality(request.technicalQuality());
        evaluation.setCommunication(request.communication());
        evaluation.setDeadlineRespect(request.deadlineRespect());
        evaluation.setAutonomy(request.autonomy());
        evaluation.setTestQuality(request.testQuality());
        evaluation.setFeedback(businessFeedback);
        evaluation.setCreatedAt(evaluatedAt);
        evaluationRepository.save(evaluation);

        MissionEvent event = new MissionEvent();
        event.setMission(mission);
        event.setFreelancer(freelancer);
        event.setType(generatedEventType);
        event.setDescription(businessFeedback);
        event.setCreatedAt(evaluatedAt);
        missionEventRepository.save(event);

        return new EvaluationResponse(
                mission.getId(),
                freelancer.getId(),
                globalNote,
                round2(evaluationScore),
                generatedEventType.name(),
                "Evaluation multi-criteres ajoutee. Event metier genere et ranking recalcule."
        );
    }

    private void validateRequest(CreateEvaluationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Evaluation request is required");
        }

        if (request.missionId() == null) {
            throw new IllegalArgumentException("Mission ID is required");
        }

        validateScore("technicalQuality", request.technicalQuality());
        validateScore("communication", request.communication());
        validateScore("deadlineRespect", request.deadlineRespect());
        validateScore("autonomy", request.autonomy());
        validateScore("testQuality", request.testQuality());
    }

    private void validateScore(String fieldName, Integer value) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        if (value < 1 || value > 5) {
            throw new IllegalArgumentException(fieldName + " must be between 1 and 5");
        }
    }

    private double calculateEvaluationScore(CreateEvaluationRequest request) {
        double recencyScore = 5.0;

        return 0.30 * request.technicalQuality()
                + 0.18 * request.communication()
                + 0.18 * request.deadlineRespect()
                + 0.14 * request.autonomy()
                + 0.10 * request.testQuality()
                + 0.10 * recencyScore;
    }

    private EventType generateEventType(double evaluationScore, int deadlineRespect) {
        if (evaluationScore <= 2.0 || deadlineRespect <= 1) {
            return EventType.CANCELLED;
        }

        if (deadlineRespect <= 2 || evaluationScore < 3.0) {
            return EventType.LATE_DELIVERY;
        }

        return EventType.SUCCESS;
    }

    private String buildBusinessFeedback(EventType type) {
        return switch (type) {
            case SUCCESS -> "Livrable validé par le client.";
            case LATE_DELIVERY -> "Livraison en retard sur le jalon.";
            case CANCELLED -> "Livrable critique ou rejeté par le client.";
        };
    }

    private int clampNote(int value) {
        return Math.max(1, Math.min(5, value));
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}