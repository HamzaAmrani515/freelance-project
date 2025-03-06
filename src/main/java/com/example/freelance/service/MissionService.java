package com.example.freelance.service;

import com.example.freelance.model.MissionFreelance;
import com.example.freelance.repository.MissionFreelanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionFreelanceRepository missionFreelanceRepository;

    // Méthode pour lier une mission à un freelance
    public MissionFreelance linkMissionToFreelancer(Long missionId, Long freelancerId) {
        MissionFreelance missionFreelance = new MissionFreelance();
        missionFreelance.setMissionId(missionId);
        missionFreelance.setFreelancerId(freelancerId);

        return missionFreelanceRepository.save(missionFreelance);
    }
}
