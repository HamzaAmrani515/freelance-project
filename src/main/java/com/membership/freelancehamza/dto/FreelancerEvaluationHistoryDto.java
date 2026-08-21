package com.membership.freelancehamza.dto;

import java.time.LocalDateTime;

public record FreelancerEvaluationHistoryDto(
        Long missionId,
        String missionTitle,
        String deliveryStatus,
        Integer note,
        Integer technicalQuality,
        Integer communication,
        Integer autonomy,
        Integer testQuality,
        Double missionScore,
        String feedback,
        LocalDateTime evaluatedAt
) {
}