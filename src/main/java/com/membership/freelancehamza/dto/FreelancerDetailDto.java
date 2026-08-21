package com.membership.freelancehamza.dto;

import java.util.List;

public record FreelancerDetailDto(
        Long freelancerId,
        String fullName,
        String skill,
        String seniority,
        String availabilityStatus,
        String companyName,
        Integer yearsExperience,

        Double finalScore,
        Double recentEvaluationScore,
        // score basse sur la fiabilite , retard , annulation ou bien succes
        Double reliabilityScore,
        // score qui indique si le freelancer baisse ou progress
        Double trendScore,
        Double experienceScore,
        Double availabilityScore,
        // tendance affich dans le front soit stable declining or improving
        String trendLabel,

        Long missionsCount,
        Long evaluationsCount,

        List<FreelancerMissionHistoryDto> missionHistory
) {}