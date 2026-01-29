package com.example.freelance.service;

import com.example.freelance.dto.NotificationDTO;
import com.example.freelance.model.Freelancer;
import com.example.freelance.model.Mission;
import com.example.freelance.model.Notification;
import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.model.enums.NotificationType;
import com.example.freelance.repository.FreelancerRepository;
import com.example.freelance.repository.MissionRepository;
import com.example.freelance.repository.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final FreelancerRepository freelancerRepository;
    private final MissionRepository missionRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               SimpMessagingTemplate messagingTemplate,
                               FreelancerRepository freelancerRepository,
                               MissionRepository missionRepository) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
        this.freelancerRepository = freelancerRepository;
        this.missionRepository = missionRepository;
    }

    @Transactional
    public Notification createAndSendNotification(Long freelancerId, Long missionId,
                                                   NotificationType type, String message) {
        Notification notification = new Notification();
        notification.setFreelancerId(freelancerId);
        notification.setMissionId(missionId);
        notification.setType(type);
        notification.setMessage(message);

        notification = notificationRepository.save(notification);
        sendNotificationViaWebSocket(notification);

        return notification;
    }

    private void sendNotificationViaWebSocket(Notification notification) {
        NotificationDTO dto = NotificationDTO.fromEntity(notification);
        String destination = "/topic/notifications/" + notification.getFreelancerId();
        messagingTemplate.convertAndSend(destination, dto);

        notification.setSentViaWebsocket(true);
        notificationRepository.save(notification);
    }

    public List<NotificationDTO> getNotificationsByFreelancerId(Long freelancerId) {
        return notificationRepository.findByFreelancerIdOrderByCreatedAtDesc(freelancerId)
                .stream()
                .map(NotificationDTO::fromEntity)
                .toList();
    }

    public List<NotificationDTO> getUnreadNotificationsByFreelancerId(Long freelancerId) {
        return notificationRepository.findByFreelancerIdAndIsReadFalseOrderByCreatedAtDesc(freelancerId)
                .stream()
                .map(NotificationDTO::fromEntity)
                .toList();
    }

    public long countUnreadNotifications(Long freelancerId) {
        return notificationRepository.countByFreelancerIdAndIsReadFalse(freelancerId);
    }

    @Transactional
    public NotificationDTO markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));

        notification.setIsRead(true);
        notification = notificationRepository.save(notification);

        return NotificationDTO.fromEntity(notification);
    }

    @Transactional
    public void markAllAsRead(Long freelancerId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByFreelancerIdAndIsReadFalseOrderByCreatedAtDesc(freelancerId);

        unreadNotifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    public void createNotification(Long freelancerId, Long missionId) {
        Optional<Freelancer> freelancerOpt = freelancerRepository.findById(freelancerId);
        Optional<Mission> missionOpt = missionRepository.findById(missionId);

        if (freelancerOpt.isEmpty() || missionOpt.isEmpty()) {
            throw new IllegalArgumentException("Freelancer or Mission not found!");
        }

        Freelancer freelancer = freelancerOpt.get();
        Mission mission = missionOpt.get();

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
