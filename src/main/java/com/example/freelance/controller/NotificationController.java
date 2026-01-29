package com.example.freelance.controller;

import com.example.freelance.dto.NotificationDTO;
import com.example.freelance.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/freelancer/{id}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByFreelancerId(@PathVariable Long id) {
        log.info("Récupérer toutes les notifications pour le freelancer {}", id);
        List<NotificationDTO> notifications = notificationService.getNotificationsByFreelancerId(id);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/freelancer/{id}/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotificationsByFreelancerId(@PathVariable Long id) {
        log.info("Récupérer les notifications non lues pour le freelancer {}", id);
        List<NotificationDTO> notifications = notificationService.getUnreadNotificationsByFreelancerId(id);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/freelancer/{id}/count")
    public ResponseEntity<Map<String, Long>> countUnreadNotifications(@PathVariable Long id) {
        log.info("Compter les notifications non lues pour le freelancer {}", id);
        long count = notificationService.countUnreadNotifications(id);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable Long id) {
        log.info("Marquer la notification {} comme lue", id);
        NotificationDTO notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(notification);
    }

    @PutMapping("/freelancer/{id}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long id) {
        log.info("Marquer toutes les notifications du freelancer {} comme lues", id);
        notificationService.markAllAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createNotification(@RequestParam Long freelancerId, @RequestParam Long missionId) {
        log.info("Créer une notification pour le freelancer {} et la mission {}", freelancerId, missionId);
        notificationService.createNotification(freelancerId, missionId);
        return ResponseEntity.ok().build();
    }
}
