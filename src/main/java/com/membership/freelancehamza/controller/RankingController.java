package com.membership.freelancehamza.controller;

import com.membership.freelancehamza.dto.FreelancerRankingDto;
import com.membership.freelancehamza.service.RankingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@CrossOrigin
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping
    public List<FreelancerRankingDto> getRanking(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        return rankingService.getRanking(page, size);
    }

    @GetMapping("/{freelancerId}")
    public FreelancerRankingDto getFreelancerRankingById(@PathVariable Long freelancerId) {
        return rankingService.getFreelancerRankingById(freelancerId);
    }
}