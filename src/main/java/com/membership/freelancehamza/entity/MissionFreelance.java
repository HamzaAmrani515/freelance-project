package com.membership.freelancehamza.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "mission_freelance")
public class MissionFreelance {

    @Id
    @Column(name = "mission_id")
    private Long missionId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "freelancer_id", nullable = false)
    private Freelancer freelancer;

    public MissionFreelance() {}

    public MissionFreelance(Mission mission, Freelancer freelancer) {
        this.mission = mission;
        this.freelancer = freelancer;
        this.missionId = mission.getId();
    }

    public Long getMissionId() { return missionId; }
    public Mission getMission() { return mission; }
    public Freelancer getFreelancer() { return freelancer; }

    public void setMissionId(Long missionId) { this.missionId = missionId; }
    public void setMission(Mission mission) { this.mission = mission; }
    public void setFreelancer(Freelancer freelancer) { this.freelancer = freelancer; }
}
