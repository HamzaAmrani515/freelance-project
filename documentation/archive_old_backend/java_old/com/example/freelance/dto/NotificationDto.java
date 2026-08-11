package com.example.freelance.dto;

import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        String message,
        LocalDateTime timestamp,
        boolean isRead,
        Long freelancerId,
        Long missionId,
        String missionTitre
) {}
