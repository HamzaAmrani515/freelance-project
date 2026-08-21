package com.membership.freelancehamza.repository;

import com.membership.freelancehamza.entity.MissionFreelance;
import com.membership.freelancehamza.entity.MissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionFreelanceRepository extends JpaRepository<MissionFreelance, Long> {

    boolean existsByFreelancer_IdAndMission_Status(Long freelancerId, MissionStatus status);

    long countByFreelancer_Id(Long freelancerId);
}