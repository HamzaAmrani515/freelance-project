package com.example.freelance.kafka.events;

import java.time.Instant;

public record MissionCreatedEvent(
        Long missionId,
        Instant createdAt,
        String source
) {}