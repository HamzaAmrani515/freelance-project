package com.example.freelance.controller;
import com.example.freelance.model.Notification;
import com.example.freelance.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/freelancer/{freelancerId}")
    public List<Notification> getNotificationsForFreelancer(@PathVariable Long freelancerId) {
        return notificationService.getNotificationsForFreelancer(freelancerId);
    }
    @PostMapping("/create")
    public void createNotification(@RequestParam Long freelancerId, @RequestParam Long missionId) {
        notificationService.createNotification(freelancerId, missionId);
    }
}

