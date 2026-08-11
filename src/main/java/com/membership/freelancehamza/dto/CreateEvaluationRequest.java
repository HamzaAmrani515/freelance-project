package com.membership.freelancehamza.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateEvaluationRequest(
        @NotNull Long missionId,

        @NotNull @Min(1) @Max(5)
        Integer technicalQuality,

        @NotNull @Min(1) @Max(5)
        Integer communication,

        @NotNull @Min(1) @Max(5)
        Integer deadlineRespect,

        @NotNull @Min(1) @Max(5)
        Integer autonomy,

        @NotNull @Min(1) @Max(5)
        Integer testQuality,

        String feedback
) {}