package com.example.freelance.service;

import com.example.freelance.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationWsService {

    private final SimpMessagingTemplate messagingTemplate;

    public void pushToFreelancer(Long freelancerId, NotificationDto dto) {

        messagingTemplate.convertAndSend("/topic/notifications/" + freelancerId, dto);
    }
}