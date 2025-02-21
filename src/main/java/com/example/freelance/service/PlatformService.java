package com.example.freelance.service;
import com.example.freelance.model.Freelancer;
import com.example.freelance.model.Mission;
import com.example.freelance.model.Platform;
import com.example.freelance.repository.PlatformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class PlatformService {

    @Autowired
    private PlatformRepository platformRepository;

    public Platform createPlatform() {
        Platform platform = new Platform();
        return platformRepository.save(platform);
    }

    public Optional<Platform> getPlatformById(Long id) {
        return platformRepository.findById(id);
    }



    public boolean isEligible(Freelancer freelancer, Mission mission) {

        if (freelancer.getExperience() < 1) {
            return false;
        }
        if (mission.getBudget() < 500) {
            return false;
        }
        if (!freelancer.getCompetences().containsAll(mission.getCompetences())) {
            return false;
        }
        return true;
    }

    public String applyForMission(Long platformId, Long freelancerId, Long missionId) {
        Platform platform = platformRepository.findById(platformId)
                .orElseThrow(() -> new RuntimeException("Platform not found"));git

        Freelancer freelancer = platform.getFreelancers().stream()
                .filter(f -> f.getId().equals(freelancerId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        Mission mission = platform.getMissions().stream()
                .filter(m -> m.getId().equals(missionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Mission not found"));

        if (isEligible(freelancer, mission)) {
            return " Candidature acceptée pour la mission: " + mission.getTitre();
        } else {
            return " Candidature refusée : le freelance ne correspond pas aux critères.";
        }
    }
}
