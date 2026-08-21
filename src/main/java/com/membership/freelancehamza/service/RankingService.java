package com.membership.freelancehamza.service;

import com.membership.freelancehamza.dto.FreelancerRankingDto;
import com.membership.freelancehamza.entity.Freelancer;
import com.membership.freelancehamza.entity.MissionStatus;
import com.membership.freelancehamza.repository.EvaluationRepository;
import com.membership.freelancehamza.repository.FreelancerRepository;
import com.membership.freelancehamza.repository.MissionFreelanceRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RankingService {

    private final FreelancerRepository freelancerRepository;
    private final EvaluationRepository evaluationRepository;
    private final MissionFreelanceRepository missionFreelanceRepository;

    public RankingService(
            FreelancerRepository freelancerRepository,
            EvaluationRepository evaluationRepository,
            MissionFreelanceRepository missionFreelanceRepository
    ) {
        this.freelancerRepository = freelancerRepository;
        this.evaluationRepository = evaluationRepository;
        this.missionFreelanceRepository = missionFreelanceRepository;
    }

    @Transactional(readOnly = true)
    public List<FreelancerRankingDto> getRanking(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 500);

        return freelancerRepository
                .findAllByOrderByFinalScoreDesc(PageRequest.of(safePage, safeSize))
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FreelancerRankingDto> getRanking() {
        return getRanking(0, 50);
    }

    @Transactional(readOnly = true)
    public FreelancerRankingDto getFreelancerRankingById(Long freelancerId) {
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
                .orElseThrow(() -> new RuntimeException("Freelancer not found: " + freelancerId));

        return toDto(freelancer);
    }

    private FreelancerRankingDto toDto(Freelancer freelancer) {
        double finalScore = freelancer.getFinalScore() == null
                ? 0.0
                : freelancer.getFinalScore();

        String availabilityStatus = isBusy(freelancer)
                ? "BUSY"
                : "AVAILABLE";

        long missionsCount = missionFreelanceRepository.countByFreelancer_Id(freelancer.getId());
        long evaluationsCount = evaluationRepository.countByFreelancer_Id(freelancer.getId());

        String fullName = ((freelancer.getPrenom() == null ? "" : freelancer.getPrenom()) + " "
                + (freelancer.getNom() == null ? "" : freelancer.getNom())).trim();

        return new FreelancerRankingDto(
                freelancer.getId(),
                fullName,
                freelancer.getSkill(),
                freelancer.getSeniority(),
                availabilityStatus,
                freelancer.getCompanyName(),
                freelancer.getYearsExperience(),
                round2(finalScore),

                0.0,
                0.0,
                0.0,
                0.0,
                0.0,

                freelancer.getTrendLabel() == null ? "STABLE" : freelancer.getTrendLabel(),
                missionsCount,
                evaluationsCount
        );
    }
// vois s'il est busy s'il est en mission
    private boolean isBusy(Freelancer freelancer) {
        return missionFreelanceRepository.existsByFreelancer_IdAndMission_Status(
                freelancer.getId(),
                MissionStatus.IN_PROGRESS
        );
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}