package com.example.freelance.service;

import com.example.freelance.dto.FreelancerRecommendationDTO;
import com.example.freelance.model.Competence;
import com.example.freelance.model.Evaluation;
import com.example.freelance.model.Freelancer;
import com.example.freelance.model.Mission;
import com.example.freelance.repository.EvaluationRepository;
import com.example.freelance.repository.FreelancerRepository;
import com.example.freelance.repository.MissionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private FreelancerRepository freelancerRepository;

    @Mock
    private EvaluationRepository evaluationRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    @DisplayName("faire une  IllegalArgumentException si la mission n'est pas trouver")
    void recommendFreelancersForMission_MissionNotFound() {
        // GIVEN
        Long missionId = 1L;
        when(missionRepository.findById(missionId)).thenReturn(java.util.Optional.empty());

        // WHEN & THEN
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationService.recommendFreelancersForMission(missionId)
        );
        assertTrue(exception.getMessage().contains("Mission non trouvée"));
    }

    @Test
    @DisplayName("retourner une liste vide si il a pas de lmission similaire")
    void recommendFreelancersForMission_NoSimilarMissions() {
        // GIVEN ( création d'un jeux de données )
        Long missionId = 1L;
        Competence competence = new Competence();
        competence.setId(100L);

        Mission mission = new Mission();
        mission.setId(missionId);
        mission.setTitre("Test Mission");
        mission.setCompetences(Set.of(competence));

        when(missionRepository.findById(missionId)).thenReturn(java.util.Optional.of(mission));
        when(missionRepository.findSimilarMissionIds(anySet(), eq(missionId), anyInt()))
                .thenReturn(Collections.emptyList());

        // WHEN
        List<FreelancerRecommendationDTO> result = recommendationService.recommendFreelancersForMission(missionId);

        // THEN
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("retourner une liste vite si il trouve pas d'evaluation ")
    void recommendFreelancersForMission_NoEvaluations() {
        // GIVEN
        Long missionId = 1L;
        Competence competence = new Competence();
        competence.setId(100L);

        Mission mission = new Mission();
        mission.setId(missionId);
        mission.setTitre("Test Mission");
        mission.setCompetences(Set.of(competence));

        when(missionRepository.findById(missionId)).thenReturn(java.util.Optional.of(mission));
        when(missionRepository.findSimilarMissionIds(anySet(), eq(missionId), anyInt()))
                .thenReturn(List.of(2L));
        when(evaluationRepository.findAllByMissionIds(anyList()))
                .thenReturn(Collections.emptyList());

        // WHEN
        List<FreelancerRecommendationDTO> result = recommendationService.recommendFreelancersForMission(missionId);

        // THEN
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("zapper un frrelance s'il a pas rouver dans le repository")
    void recommendFreelancersForMission_FreelancerNotFound() {
        // GIVEN
        Long missionId = 1L;
        Competence competence = new Competence();
        competence.setId(100L);

        Mission mission = new Mission();
        mission.setId(missionId);
        mission.setTitre("Test Mission");
        mission.setCompetences(Set.of(competence));

        when(missionRepository.findById(missionId)).thenReturn(java.util.Optional.of(mission));
        when(missionRepository.findSimilarMissionIds(anySet(), eq(missionId), anyInt()))
                .thenReturn(List.of(2L));

        Evaluation evaluation = new Evaluation();
        evaluation.setNote(5.0);
        Freelancer freelancer = new Freelancer();
        freelancer.setId(10L);
        evaluation.setFreelancer(freelancer);

        when(evaluationRepository.findAllByMissionIds(anyList())).thenReturn(List.of(evaluation));
        // Simuler un cas où aucun freelance n'est trouvé
        when(freelancerRepository.findAllWithCompetencesByIdIn(anySet()))
                .thenReturn(Collections.emptyList());

        // WHEN
        List<FreelancerRecommendationDTO> result = recommendationService.recommendFreelancersForMission(missionId);

        // THEN
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("retourner une liste vide si le freelance n'a pas assez de competence ")
    void recommendFreelancersForMission_NotEnoughCommonCompetences() {
        // GIVEN
        Long missionId = 1L;
        Competence competence1 = new Competence();
        competence1.setId(100L);
        Competence competence2 = new Competence();
        competence2.setId(101L);

        Mission mission = new Mission();
        mission.setId(missionId);
        mission.setTitre("Test Mission");
        // La mission requiert 2 compétences
        mission.setCompetences(Set.of(competence1, competence2));

        when(missionRepository.findById(missionId)).thenReturn(java.util.Optional.of(mission));
        when(missionRepository.findSimilarMissionIds(anySet(), eq(missionId), anyInt()))
                .thenReturn(List.of(2L));

        // Le freelance ne possède qu'une seule des compétences requises
        Freelancer freelancer = new Freelancer();
        freelancer.setId(10L);
        freelancer.setNom("Doe");
        freelancer.setPrenom("Jane");
        freelancer.setCompetences(Set.of(competence1));
        freelancer.setExperience(2.0);

        Evaluation evaluation = new Evaluation();
        evaluation.setNote(4.0);
        evaluation.setFreelancer(freelancer);

        when(evaluationRepository.findAllByMissionIds(anyList())).thenReturn(List.of(evaluation));
        when(freelancerRepository.findAllWithCompetencesByIdIn(Set.of(10L)))
                .thenReturn(List.of(freelancer));

        // WHEN
        List<FreelancerRecommendationDTO> result = recommendationService.recommendFreelancersForMission(missionId);

        // THEN
        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("retourner le freelance recommander avec le score calculer ")
    void recommendFreelancersForMission_ValidRecommendation() {

        Long missionId = 1L;

        // Création des compétences requises pour la mission
        Competence competence1 = new Competence();
        competence1.setId(100L);
        Competence competence2 = new Competence();
        competence2.setId(101L);
        Set<Competence> missionCompetences = Set.of(competence1, competence2);

        Mission mission = new Mission();
        mission.setId(missionId);
        mission.setTitre("Mission Test");
        mission.setCompetences(missionCompetences);

        when(missionRepository.findById(missionId)).thenReturn(java.util.Optional.of(mission));
        // On s'assure que la méthode de recherche de missions similaires est appelée avec les bons paramètres
        Set<Long> competenceIds = missionCompetences.stream().map(Competence::getId).collect(Collectors.toSet());
        when(missionRepository.findSimilarMissionIds(eq(competenceIds), eq(missionId), anyInt()))
                .thenReturn(List.of(2L));

        // Création d'un freelance possédant les compétences requises
        Freelancer freelancer = new Freelancer();
        freelancer.setId(10L);
        freelancer.setNom("Doe");
        freelancer.setPrenom("John");
        freelancer.setCompetences(Set.of(competence1, competence2));
        freelancer.setExperience(3.0);

        // Création de deux évaluations pour ce freelance
        Evaluation evaluation1 = new Evaluation();
        evaluation1.setNote(4.0);
        evaluation1.setFreelancer(freelancer);

        Evaluation evaluation2 = new Evaluation();
        evaluation2.setNote(4.0);
        evaluation2.setFreelancer(freelancer);

        when(evaluationRepository.findAllByMissionIds(anyList()))
                .thenReturn(List.of(evaluation1, evaluation2));
        when(freelancerRepository.findAllWithCompetencesByIdIn(Set.of(10L)))
                .thenReturn(List.of(freelancer));

        // WHEN
        List<FreelancerRecommendationDTO> result = recommendationService.recommendFreelancersForMission(missionId);

        // THEN
        //assertion pour faire les vérification
        assertNotNull(result);
        assertEquals(1, result.size());
        FreelancerRecommendationDTO recommendation = result.get(0);
        assertEquals(10L, recommendation.getFreelancerId());
        assertEquals("Doe", recommendation.getNom());
        assertEquals("John", recommendation.getPrenom());

        /*
         * Calcul du score attendu :
         * - Nombre de compétences en commun (commonCount) = 2
         * - Expérience = 3.0
         * - Note moyenne = (4.0 + 4.0) / 2 = 4.0
         * Score = (2 * 2) + 3.0 + (4.0 * 2) = 4 + 3 + 8 = 15
         */
        assertEquals(15, recommendation.getScore());
    }
}
