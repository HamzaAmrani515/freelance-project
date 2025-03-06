package com.example.freelance.model;

import com.example.freelance.event.MissionCreatedEvent;
import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.service.FreelanceService;
import com.example.freelance.service.NotificationService;
import jakarta.persistence.PostPersist;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class MissionListener {

    private final ApplicationEventPublisher eventPublisher;
      private static FreelanceService freelanceService;
      private static NotificationService notificationService;


    public MissionListener(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostPersist
    public void afterMissionAdded(Mission mission) {
        System.out.println("MissionListener triggered for mission: " + mission.getId());

        Mission fullMission = freelanceService.getMissionWithCompetences(mission.getId());
        Set<Competence> competences = fullMission.getCompetences();

        if (mission.getStatut() != MissionStatut.EN_ATTENTE) {
            System.out.println(" Mission is not in EN_ATTENTE status. Skipping notifications.");
            return;
        }

        List<Freelancer> freelancers = freelanceService.getAllFreelancers();
        System.out.println(" Found " + freelancers.size() + " freelancers to check.");

        for (Freelancer freelancer : freelancers) {
            Set<Competence> freelancerCompetences = freelancer.getCompetences();
            System.out.println(" Checking freelancer: " + freelancer.getNom());

            if (freelancerCompetences.containsAll(competences)
                    && freelanceService.isFreelancerAvailable(freelancer)) {

                System.out.println(" Freelancer " + freelancer.getNom() + " matches and is available.");
                notificationService.sendNotification(freelancer, mission);
            } else {
                System.out.println(" Freelancer " + freelancer.getNom() + " does not match the required skills or is unavailable.");
            }
        }

}
}
