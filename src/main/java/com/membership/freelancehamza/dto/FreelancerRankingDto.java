package com.membership.freelancehamza.dto;

public record FreelancerRankingDto(
        Long freelancerId,
        String fullName,
        String skill,
        String seniority,
        String availabilityStatus,
        String companyName,
        Integer yearsExperience,
        Double finalScore,
        Double recentEvaluationScore,
        Double reliabilityScore,
        Double trendScore,
        Double experienceScore,
        Double availabilityScore,
        String trendLabel,
        Long missionsCount,
        Long evaluationsCount
) {}