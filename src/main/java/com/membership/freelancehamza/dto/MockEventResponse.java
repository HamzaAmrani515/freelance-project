package com.membership.freelancehamza.dto;

public record MockEventResponse(
        Long missionId,
        Long freelancerId,
        String type,
        Integer noteInserted,
        String message
) {}
