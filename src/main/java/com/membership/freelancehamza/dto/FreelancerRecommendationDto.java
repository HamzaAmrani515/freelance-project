package com.membership.freelancehamza.dto;

public record FreelancerRecommendationDto(
        Long freelancerId,
        String fullName,
        String skill,
        String seniority,
        String availabilityStatus,
        String companyName,
        Integer yearsExperience,
        Double finalScore,
        String trendLabel
) {
}