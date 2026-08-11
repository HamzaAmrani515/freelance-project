package com.membership.freelancehamza.controller;

import com.membership.freelancehamza.dto.FreelancerRankingDto;
import com.membership.freelancehamza.service.RankingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@CrossOrigin
public class RankingController {

    private final RankingService service;

    public RankingController(RankingService service) {
        this.service = service;
    }

    @GetMapping
    public List<FreelancerRankingDto> getRanking(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return service.getRanking(page, size);
    }
}