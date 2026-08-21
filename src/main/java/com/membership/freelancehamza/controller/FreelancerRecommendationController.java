package com.membership.freelancehamza.controller;

import com.membership.freelancehamza.dto.FreelancerRecommendationDto;
import com.membership.freelancehamza.service.FreelancerRecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FreelancerRecommendationController {

    private final FreelancerRecommendationService recommendationService;

    public FreelancerRecommendationController(FreelancerRecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/api/client/freelancers/recommendations")
    public List<FreelancerRecommendationDto> recommendFreelancers(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String seniority,
            @RequestParam(required = false) Integer minExperience
    ) {
        return recommendationService.recommendFreelancers(
                skill,
                seniority,
                minExperience
        );
    }
}