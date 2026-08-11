package com.membership.freelancehamza.service;

import com.membership.freelancehamza.dto.FreelancerRankingDto;
import com.membership.freelancehamza.repository.RankingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankingService {

    private final RankingRepository repo;

    public RankingService(RankingRepository repo) {
        this.repo = repo;
    }

    // Utilisé par /api/ranking?page=0&size=20
    public List<FreelancerRankingDto> getRanking(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        int offset = safePage * safeSize;

        return repo.findRanking(safeSize, offset).stream()
                .map(this::toDto)
                .toList();
    }

    // Compatibilité avec FreelancerDetailService qui appelle encore getRanking()
    public List<FreelancerRankingDto> getRanking() {
        return repo.findRanking(10000, 0).stream()
                .map(this::toDto)
                .toList();
    }

    private FreelancerRankingDto toDto(com.membership.freelancehamza.repository.FreelancerScoreRow r) {
        return new FreelancerRankingDto(
                r.getFreelancerId(),
                r.getFullName(),
                r.getSkill(),
                r.getSeniority(),
                r.getAvailabilityStatus(),
                r.getCompanyName(),
                r.getYearsExperience(),
                r.getFinalScore(),
                r.getRecentEvaluationScore(),
                r.getReliabilityScore(),
                r.getTrendScore(),
                r.getExperienceScore(),
                r.getAvailabilityScore(),
                r.getTrendLabel(),
                r.getMissionsCount(),
                r.getEvaluationsCount()
        );
    }
}