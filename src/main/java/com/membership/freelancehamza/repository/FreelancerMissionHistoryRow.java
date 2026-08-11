package com.membership.freelancehamza.repository;

import java.time.LocalDateTime;

public interface FreelancerMissionHistoryRow {
    Long getMissionId();
    String getMissionTitle();
    String getEventType();
    Integer getNote();
    Integer getTechnicalQuality();
    Integer getCommunication();
    Integer getDeadlineRespect();
    Integer getAutonomy();
    Integer getTestQuality();
    Double getMissionScore();
    String getFeedback();
    LocalDateTime getEvaluatedAt();
}