package com.example.freelance.model;

import com.example.freelance.service.FreelanceService;
import com.example.freelance.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.persistence.PostPersist;
import java.util.List;
import java.util.Set;

@Component
public class MissionListener {

    @Autowired
    private FreelanceService freelanceService;

    @Autowired
    private NotificationService notificationService;

    @PostPersist
    public void afterMissionAdded(Mission mission) {
        // Get all freelancers
        List<Freelancer> freelancers = freelanceService.getAllFreelancers();

        for (Freelancer freelancer : freelancers) {
            Set<Competence> freelancerCompetences = freelancer.getCompetences();

            // Check if freelancer has ALL required competencies
            if (freelancerCompetences.containsAll(mission.getCompetences())) {
                notificationService.sendNotification(freelancer, mission);
            }
        }
    }
}
