package com.example.freelance.service;

import com.example.freelance.event.MissionCreatedEvent;
import com.example.freelance.model.Freelancer;
import com.example.freelance.model.Mission;
import com.example.freelance.model.Competence;
import com.example.freelance.model.enums.MissionStatut;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class MissionEventListener {

    private final FreelanceService freelanceService;
    private final NotificationService notificationService;

    public MissionEventListener(FreelanceService freelanceService, NotificationService notificationService) {
        this.freelanceService = freelanceService;
        this.notificationService = notificationService;
    }

    @Async // Exécuté en arrière-plan pour éviter de bloquer le processus principal
    @EventListener
    public void handleMissionCreatedEvent(MissionCreatedEvent event) {
        Mission mission = event.getMission();

        if (mission.getStatut() != MissionStatut.EN_ATTENTE) {
            return;
        }

        Mission fullMission = freelanceService.getMissionWithCompetences(mission.getId());
        Set<Competence> competences = fullMission.getCompetences();

        List<Freelancer> freelancers = freelanceService.getAllFreelancers();

        for (Freelancer freelancer : freelancers) {
            Set<Competence> freelancerCompetences = freelancer.getCompetences();

            if (freelancerCompetences.containsAll(competences)
                    && freelanceService.isFreelancerAvailable(freelancer)) {
                notificationService.sendNotification(freelancer, mission);
            }
        }
    }
}
