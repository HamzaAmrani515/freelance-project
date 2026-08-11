package com.membership.freelancehamza.dto;

public record EvaluationResponse(
        Long missionId,
        Long freelancerId,
        Integer globalNote,
        Double evaluationScore,
        String generatedEventType,
        String message
) {}