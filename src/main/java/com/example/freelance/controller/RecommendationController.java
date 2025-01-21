package com.example.freelance.controller;

import com.example.freelance.dto.FreelancerRecommendationDTO;
import com.example.freelance.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommandations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/mission/{missionId}")
    public List<FreelancerRecommendationDTO> getRecommendations(@PathVariable Long missionId) {
        return recommendationService.recommendFreelancersForMission(missionId);
    }
}
