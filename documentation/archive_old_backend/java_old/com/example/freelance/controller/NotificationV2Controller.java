package com.example.freelance.controller;

import com.example.freelance.dto.NotificationDto;
import com.example.freelance.service.NotificationV2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications/v2")
public class NotificationV2Controller {

    private final NotificationV2Service notificationV2Service;

    @GetMapping("/freelancer/{freelancerId}")
    public List<NotificationDto> byFreelancer(@PathVariable Long freelancerId) {
        return notificationV2Service.getByFreelancer(freelancerId);
    }

    @GetMapping("/freelancer/{freelancerId}/unread-count")
    public long unreadCount(@PathVariable Long freelancerId) {
        return notificationV2Service.unreadCount(freelancerId);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationV2Service.markAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/mission/{missionId}/dispatch")
    public ResponseEntity<String> dispatch(@PathVariable Long missionId) {
        int count = notificationV2Service.dispatchForMission(missionId);
        return ResponseEntity.ok("Notifications créées: " + count);
    }
}
