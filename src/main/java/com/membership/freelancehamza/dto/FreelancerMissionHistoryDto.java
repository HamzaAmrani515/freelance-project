package com.membership.freelancehamza.dto;

import java.time.LocalDateTime;

public record FreelancerMissionHistoryDto(
        Long missionId,
        String missionTitle,
        String eventType,
        Integer note,
        Integer technicalQuality,
        Integer communication,
        Integer deadlineRespect,
        Integer autonomy,
        Integer testQuality,
        Double missionScore,
        String feedback,
        LocalDateTime evaluatedAt
) {}