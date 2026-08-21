package com.membership.freelancehamza.dto;

public record EvaluationResponse(
        Long missionId,
        Long freelancerId,
        Integer globalNote,
        Double evaluationScore,
        Double finalScore,
        String deliveryStatus,
        String trendLabel,
        String message
) {
}