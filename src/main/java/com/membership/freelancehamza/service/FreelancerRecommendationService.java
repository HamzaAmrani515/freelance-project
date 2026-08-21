package com.membership.freelancehamza.service;

import com.membership.freelancehamza.dto.FreelancerRecommendationDto;
import com.membership.freelancehamza.entity.Freelancer;
import com.membership.freelancehamza.entity.MissionStatus;
import com.membership.freelancehamza.repository.FreelancerRepository;
import com.membership.freelancehamza.repository.MissionFreelanceRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FreelancerRecommendationService {

    private final FreelancerRepository freelancerRepository;
    private final MissionFreelanceRepository missionFreelanceRepository;

    public FreelancerRecommendationService(
            FreelancerRepository freelancerRepository,
            MissionFreelanceRepository missionFreelanceRepository
    ) {
        this.freelancerRepository = freelancerRepository;
        this.missionFreelanceRepository = missionFreelanceRepository;
    }

    /*
     * Cette methode permet de recommander des freelancers à un client.
     *
     * Le client peut choisir
     * - un skill recherché
     * - une seniorite
     * - une experience minimale
     *
     * Ensuite, l'algorithme garde seulement les freelancers disponibles,
     * applique les filtres demandes, puis retourne les 5 meilleurs profils.
     */
    @Transactional(readOnly = true)
    public List<FreelancerRecommendationDto> recommendFreelancers(
            String skill,
            String seniority,
            Integer minExperience
    ) {

        List<Freelancer> candidates = loadCandidates(skill);


        return candidates.stream()
                .filter(freelancer -> !isBusy(freelancer))
                .filter(freelancer -> matchSeniority(freelancer, seniority))
                .filter(freelancer -> matchExperience(freelancer, minExperience))
                .limit(5)
                .map(this::toDto)
                .toList();
    }

    // Cette méthode charge les candidats depuis la base de donnees si aucun skill n'est demande on charge tou sinon on charge selon les skills

    private List<Freelancer> loadCandidates(String skill) {
        if (skill == null || skill.isBlank()) {
            return freelancerRepository.findAllByOrderByFinalScoreDesc(
                    PageRequest.of(0, 300)
            );
        }

        return freelancerRepository.findBySkillContainingIgnoreCaseOrderByFinalScoreDesc(
                skill.trim(),
                PageRequest.of(0, 300)
        );
    }

    /*
     * Cette méthode vérifie si un freelancer est déjà occupe donc on verfie si le freelancer est ossupe et en prend que ceux qqui availble
     */
    private boolean isBusy(Freelancer freelancer) {
        return missionFreelanceRepository.existsByFreelancer_IdAndMission_Status(
                freelancer.getId(),
                MissionStatus.IN_PROGRESS
        );
    }

    // si le client cghreche par senoirity
    private boolean matchSeniority(Freelancer freelancer, String seniority) {
        if (seniority == null || seniority.isBlank()) {
            return true;
        }

        if (freelancer.getSeniority() == null) {
            return false;
        }

        return freelancer.getSeniority().equalsIgnoreCase(seniority.trim());
    }

    // experience
    private boolean matchExperience(Freelancer freelancer, Integer minExperience) {
        if (minExperience == null) {
            return true;
        }

        if (freelancer.getYearsExperience() == null) {
            return false;
        }

        return freelancer.getYearsExperience() >= minExperience;
    }


    private FreelancerRecommendationDto toDto(Freelancer freelancer) {
        return new FreelancerRecommendationDto(
                freelancer.getId(),
                freelancer.getPrenom() + " " + freelancer.getNom(),
                freelancer.getSkill(),
                freelancer.getSeniority(),
                "AVAILABLE",
                freelancer.getCompanyName(),
                freelancer.getYearsExperience(),
                round2(freelancer.getFinalScore() == null ? 0.0 : freelancer.getFinalScore()),
                freelancer.getTrendLabel() == null ? "STABLE" : freelancer.getTrendLabel()
        );
    }


    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}