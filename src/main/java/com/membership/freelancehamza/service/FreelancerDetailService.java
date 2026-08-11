package com.membership.freelancehamza.service;

import com.membership.freelancehamza.dto.FreelancerDetailDto;
import com.membership.freelancehamza.dto.FreelancerMissionHistoryDto;
import com.membership.freelancehamza.dto.FreelancerRankingDto;
import com.membership.freelancehamza.repository.FreelancerDetailRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FreelancerDetailService {

    private final RankingService rankingService;
    private final FreelancerDetailRepository freelancerDetailRepository;

    public FreelancerDetailService(
            RankingService rankingService,
            FreelancerDetailRepository freelancerDetailRepository
    ) {
        this.rankingService = rankingService;
        this.freelancerDetailRepository = freelancerDetailRepository;
    }

    public FreelancerDetailDto getFreelancerDetails(Long freelancerId) {
        FreelancerRankingDto ranking = rankingService.getRanking().stream()
                .filter(f -> f.freelancerId().equals(freelancerId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Freelancer not found: " + freelancerId));

        List<FreelancerMissionHistoryDto> history = freelancerDetailRepository.findMissionHistory(freelancerId)
                .stream()
                .map(row -> new FreelancerMissionHistoryDto(
                        row.getMissionId(),
                        row.getMissionTitle(),
                        row.getEventType(),
                        row.getNote(),
                        row.getTechnicalQuality(),
                        row.getCommunication(),
                        row.getDeadlineRespect(),
                        row.getAutonomy(),
                        row.getTestQuality(),
                        row.getMissionScore(),
                        row.getFeedback(),
                        row.getEvaluatedAt()
                ))
                .toList();

        return new FreelancerDetailDto(
                ranking.freelancerId(),
                ranking.fullName(),
                ranking.skill(),
                ranking.seniority(),
                ranking.availabilityStatus(),
                ranking.companyName(),
                ranking.yearsExperience(),

                ranking.finalScore(),
                ranking.recentEvaluationScore(),
                ranking.reliabilityScore(),
                ranking.trendScore(),
                ranking.experienceScore(),
                ranking.availabilityScore(),
                ranking.trendLabel(),

                ranking.missionsCount(),
                ranking.evaluationsCount(),

                history
        );
    }
}