package com.example.freelance.service;

import com.example.freelance.model.Freelancer;
import com.example.freelance.model.Mission;
import com.example.freelance.model.Notification;
import com.example.freelance.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public void sendNotification(Freelancer freelancer, Mission mission) {
        String message = "Nouvelle mission disponible: " + mission.getTitre();

        Notification notification = new Notification(message, freelancer, mission);
        notificationRepository.save(notification);

        System.out.println("Notification envoyée à " + freelancer.getNom() + " pour la mission: " + mission.getTitre());
    }

    public List<Notification> getNotificationsForFreelancer(Long freelancerId) {
        return notificationRepository.findByFreelancerId(freelancerId);
    }
}
