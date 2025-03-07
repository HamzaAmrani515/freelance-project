package com.example.freelance.service;

import com.example.freelance.model.Freelancer;
import com.example.freelance.model.Mission;
import com.example.freelance.model.Notification;
import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.repository.FreelancerRepository;
import com.example.freelance.repository.MissionRepository;
import com.example.freelance.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private MissionRepository missionRepository;

    public void createNotification(Long freelancerId, Long missionId) {

        Optional<Freelancer> freelancerOpt = freelancerRepository.findById(freelancerId);
        Optional<Mission> missionOpt = missionRepository.findById(missionId);

        if (freelancerOpt.isEmpty() || missionOpt.isEmpty()) {
            throw new IllegalArgumentException("Freelancer or Mission not found!");
        }

        Freelancer freelancer = freelancerOpt.get();
        Mission mission = missionOpt.get();

        // Create and save the notification
        Notification notification = new Notification("A new mission matching your skills is available!", freelancer, mission);
        notification.setIsRead(false);
        notification.setTimestamp(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    public void sendNotification(Freelancer freelancer, Mission mission) {
        if (mission.getStatut() != MissionStatut.EN_ATTENTE) {
            return;
        }

        Notification notification = new Notification("Nouvelle mission disponible: " + mission.getTitre(), freelancer, mission);
        notificationRepository.save(notification);

        System.out.println("Notification envoyée à " + freelancer.getNom() + " pour la mission: " + mission.getTitre());
    }

    public List<Notification> getNotificationsForFreelancer(Long freelancerId) {
        return notificationRepository.findByFreelancerId(freelancerId);
    }

    public void saveNotification(Notification notification) {
        notificationRepository.save(notification);
    }
}

