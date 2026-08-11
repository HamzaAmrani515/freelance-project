package com.membership.freelancehamza.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MockEventRequest(
        @NotNull Long missionId,
        @NotBlank String type,
        String description
) {}
