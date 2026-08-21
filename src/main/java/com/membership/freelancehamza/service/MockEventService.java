package com.membership.freelancehamza.service;

import com.membership.freelancehamza.dto.BatchMockEventRequest;
import com.membership.freelancehamza.dto.MockEventRequest;
import com.membership.freelancehamza.dto.MockEventResponse;
import com.membership.freelancehamza.entity.DeliveryStatus;
import com.membership.freelancehamza.entity.Evaluation;
import com.membership.freelancehamza.entity.EventType;
import com.membership.freelancehamza.entity.Freelancer;
import com.membership.freelancehamza.entity.Mission;
import com.membership.freelancehamza.entity.MissionEvent;
import com.membership.freelancehamza.entity.MissionFreelance;
import com.membership.freelancehamza.entity.MissionStatus;
import com.membership.freelancehamza.repository.EvaluationRepository;
import com.membership.freelancehamza.repository.FreelancerRepository;
import com.membership.freelancehamza.repository.MissionEventRepository;
import com.membership.freelancehamza.repository.MissionFreelanceRepository;
import com.membership.freelancehamza.repository.MissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MockEventService {

    private final MissionRepository missionRepository;
    private final MissionFreelanceRepository missionFreelanceRepository;
    private final MissionEventRepository missionEventRepository;
    private final EvaluationRepository evaluationRepository;
    private final FreelancerRepository freelancerRepository;

    public MockEventService(
            MissionRepository missionRepository,
            MissionFreelanceRepository missionFreelanceRepository,
            MissionEventRepository missionEventRepository,
            EvaluationRepository evaluationRepository,
            FreelancerRepository freelancerRepository
    ) {
        this.missionRepository = missionRepository;
        this.missionFreelanceRepository = missionFreelanceRepository;
        this.missionEventRepository = missionEventRepository;
        this.evaluationRepository = evaluationRepository;
        this.freelancerRepository = freelancerRepository;
    }

    @Transactional
    public MockEventResponse createMockEvent(MockEventRequest req) {
        Mission mission = missionRepository.findById(req.missionId())
                .orElseThrow(() -> new RuntimeException("Mission not found: " + req.missionId()));

        MissionFreelance mf = missionFreelanceRepository.findById(mission.getId())
                .orElseThrow(() -> new RuntimeException("No freelancer linked to mission: " + mission.getId()));

        Freelancer freelancer = mf.getFreelancer();
        EventType type = parseEventType(req.type());

        MissionEvent event = new MissionEvent();
        event.setMission(mission);
        event.setFreelancer(freelancer);
        event.setType(type);
        event.setDescription(req.description());
        missionEventRepository.save(event);

        List<Evaluation> oldEvaluations = evaluationRepository
                .findByFreelancer_IdOrderByCreatedAtDesc(freelancer.getId());

        Evaluation previousEvaluation = oldEvaluations.isEmpty() ? null : oldEvaluations.get(0);

        Evaluation eval = buildEvaluation(type, mission, freelancer);
        evaluationRepository.save(eval);

        double newScore = calculateEvaluationScore(eval);
        double previousScore = previousEvaluation == null ? newScore : calculateEvaluationScore(previousEvaluation);

        double finalScore = round2((0.70 * newScore + 0.30 * previousScore) * 20);
        String trendLabel = calculateTrendLabel(newScore, previousScore, previousEvaluation);

        freelancer.setFinalScore(finalScore);
        freelancer.setTrendLabel(trendLabel);
        freelancerRepository.save(freelancer);

        if (type == EventType.CANCELLED) {
            mission.setStatus(MissionStatus.CANCELLED);
        } else {
            mission.setStatus(MissionStatus.COMPLETED);
        }

        missionRepository.save(mission);

        return new MockEventResponse(
                mission.getId(),
                freelancer.getId(),
                type.name(),
                eval.getNote(),
                "Mock event ajoute. Evaluation et score final mis a jour."
        );
    }

    @Transactional
    public List<MockEventResponse> createMockEventsBatch(BatchMockEventRequest req) {
        if (req == null || req.events() == null || req.events().isEmpty()) {
            throw new IllegalArgumentException("La liste des events est vide");
        }

        return req.events().stream()
                .map(this::createMockEvent)
                .toList();
    }

    private EventType parseEventType(String type) {
        try {
            return EventType.valueOf(type.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Type invalide: SUCCESS, LATE_DELIVERY ou CANCELLED");
        }
    }

    private Evaluation buildEvaluation(EventType type, Mission mission, Freelancer freelancer) {
        Evaluation eval = new Evaluation();
        eval.setMission(mission);
        eval.setFreelancer(freelancer);

        if (type == EventType.SUCCESS) {
            eval.setNote(5);
            eval.setTechnicalQuality(5);
            eval.setCommunication(5);
            eval.setAutonomy(4);
            eval.setTestQuality(5);
            eval.setDeliveryStatus(DeliveryStatus.ON_TIME);
            eval.setFeedback("Mission livree avec succes.");
            return eval;
        }

        if (type == EventType.LATE_DELIVERY) {
            eval.setNote(3);
            eval.setTechnicalQuality(3);
            eval.setCommunication(3);
            eval.setAutonomy(3);
            eval.setTestQuality(3);
            eval.setDeliveryStatus(DeliveryStatus.LATE_DELIVERY);
            eval.setFeedback("Mission livree avec retard.");
            return eval;
        }

        eval.setNote(1);
        eval.setTechnicalQuality(1);
        eval.setCommunication(2);
        eval.setAutonomy(1);
        eval.setTestQuality(1);
        eval.setDeliveryStatus(DeliveryStatus.CANCELLED);
        eval.setFeedback("Mission annulee.");
        return eval;
    }

    private double calculateEvaluationScore(Evaluation eval) {
        return 0.30 * valueOrNote(eval.getTechnicalQuality(), eval.getNote())
                + 0.20 * valueOrNote(eval.getCommunication(), eval.getNote())
                + 0.15 * valueOrNote(eval.getAutonomy(), eval.getNote())
                + 0.15 * valueOrNote(eval.getTestQuality(), eval.getNote())
                + 0.20 * deliveryScore(eval.getDeliveryStatus());
    }

    private double deliveryScore(DeliveryStatus status) {
        if (status == DeliveryStatus.LATE_DELIVERY) {
            return 2.5;
        }

        if (status == DeliveryStatus.CANCELLED) {
            return 0.0;
        }

        return 5.0;
    }

    private String calculateTrendLabel(double newScore, double previousScore, Evaluation previousEvaluation) {
        if (previousEvaluation == null) {
            return "STABLE";
        }

        double difference = newScore - previousScore;

        if (difference > 0.2) {
            return "IMPROVING";
        }

        if (difference < -0.2) {
            return "DECLINING";
        }

        return "STABLE";
    }

    private double valueOrNote(Integer value, Integer note) {
        if (value != null) {
            return value;
        }

        if (note != null) {
            return note;
        }

        return 0;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}