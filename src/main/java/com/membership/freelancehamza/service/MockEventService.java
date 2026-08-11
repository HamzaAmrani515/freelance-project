package com.membership.freelancehamza.service;

import com.membership.freelancehamza.dto.BatchMockEventRequest;
import com.membership.freelancehamza.dto.MockEventRequest;
import com.membership.freelancehamza.dto.MockEventResponse;
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

import java.util.List;

@Service
public class MockEventService {

    private final MissionRepository missionRepository;
    private final MissionFreelanceRepository missionFreelanceRepository;
    private final MissionEventRepository missionEventRepository;
    private final EvaluationRepository evaluationRepository;

    public MockEventService(
            MissionRepository missionRepository,
            MissionFreelanceRepository missionFreelanceRepository,
            MissionEventRepository missionEventRepository,
            EvaluationRepository evaluationRepository
    ) {
        this.missionRepository = missionRepository;
        this.missionFreelanceRepository = missionFreelanceRepository;
        this.missionEventRepository = missionEventRepository;
        this.evaluationRepository = evaluationRepository;
    }

    private int noteForType(EventType type) {
        return switch (type) {
            case SUCCESS -> 5;
            case LATE_DELIVERY -> 2;
            case CANCELLED -> 1;
        };
    }

    @Transactional
    public MockEventResponse createMockEvent(MockEventRequest req) {

        Mission mission = missionRepository.findById(req.missionId())
                .orElseThrow(() -> new RuntimeException("Mission not found: " + req.missionId()));

        MissionFreelance mf = missionFreelanceRepository.findById(mission.getId())
                .orElseThrow(() -> new RuntimeException("No freelancer linked to mission: " + mission.getId()));

        Freelancer freelancer = mf.getFreelancer();

        EventType type;
        try {
            type = EventType.valueOf(req.type().trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Type invalide (SUCCESS/LATE_DELIVERY/CANCELLED)");
        }

        MissionEvent event = new MissionEvent();
        event.setMission(mission);
        event.setFreelancer(freelancer);
        event.setType(type);
        event.setDescription(req.description());
        missionEventRepository.save(event);

        int note = noteForType(type);

        Evaluation eval = new Evaluation();
        eval.setMission(mission);
        eval.setFreelancer(freelancer);
        eval.setNote(note);

        switch (type) {
            case SUCCESS -> {
                eval.setTechnicalQuality(5);
                eval.setCommunication(5);
                eval.setDeadlineRespect(5);
                eval.setAutonomy(4);
                eval.setTestQuality(5);
                eval.setFeedback("Mission livree avec succes.");
            }
            case LATE_DELIVERY -> {
                eval.setTechnicalQuality(3);
                eval.setCommunication(3);
                eval.setDeadlineRespect(2);
                eval.setAutonomy(3);
                eval.setTestQuality(3);
                eval.setFeedback("Mission livree avec retard.");
            }
            case CANCELLED -> {
                eval.setTechnicalQuality(1);
                eval.setCommunication(2);
                eval.setDeadlineRespect(1);
                eval.setAutonomy(1);
                eval.setTestQuality(1);
                eval.setFeedback("Mission annulee.");
            }
        }

        evaluationRepository.save(eval);

        return new MockEventResponse(
                mission.getId(),
                freelancer.getId(),
                type.name(),
                note,
                "Event + evaluation multi-criteres ajoutes. Le ranking doit changer."
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
}