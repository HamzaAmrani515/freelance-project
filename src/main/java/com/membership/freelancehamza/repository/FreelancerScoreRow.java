package com.membership.freelancehamza.repository;

public interface FreelancerScoreRow {
    Long getFreelancerId();
    String getFullName();
    String getSkill();
    String getSeniority();
    String getAvailabilityStatus();
    String getCompanyName();
    Integer getYearsExperience();

    Double getFinalScore();
    Double getRecentEvaluationScore();
    Double getReliabilityScore();
    Double getTrendScore();
    Double getExperienceScore();
    Double getAvailabilityScore();

    String getTrendLabel();

    Long getMissionsCount();
    Long getEvaluationsCount();
}