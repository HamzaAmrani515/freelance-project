package com.example.freelance.service.listeners;
import com.example.freelance.model.Freelancer;
import com.example.freelance.model.Mission;
import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.service.FreelanceService;

import com.example.freelance.service.NotificationService;
import jakarta.persistence.PostPersist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MissionListener {

    @Lazy
    @Autowired
    private FreelanceService freelanceService;

    @Lazy
    @Autowired
    private NotificationService notificationService;

    private final ApplicationEventPublisher eventPublisher;


    public MissionListener(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostPersist
    public void afterMissionAdded(Mission mission) {
        System.out.println("MissionListener triggered for mission: " + mission.getId());

        if (mission.getStatut() != MissionStatut.EN_ATTENTE) {
            System.out.println(" Mission is not in EN_ATTENTE status. Skipping notifications.");
            return;
        }

        List<Freelancer> freelancers = freelanceService.getAllAvailableFreelancers();

        System.out.println(" Found " + freelancers.size() + " freelancers to check.");

        for (Freelancer freelancer : freelancers) {
            System.out.println(" Checking freelancer: " + freelancer.getNom());

            notificationService.createNotification(freelancer.getId(), mission.getId());


        }
    }}
