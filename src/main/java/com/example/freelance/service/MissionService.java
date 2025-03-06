//package com.example.freelance.service;
//
//import com.example.freelance.model.MissionFreelance;
//import com.example.freelance.repository.MissionFreelanceRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class MissionService {
//
//    private final MissionFreelanceRepository missionFreelanceRepository;
//
//
//    public MissionFreelance linkMissionToFreelancer(Long missionId, Long freelancerId) {
//        MissionFreelance missionFreelance = new MissionFreelance();
//        missionFreelance.setMissionId(missionId);
//        missionFreelance.setFreelancerId(freelancerId);
//
//        return missionFreelanceRepository.save(missionFreelance);
//    }
//
//}
package com.example.freelance.service;

import com.example.freelance.model.Mission;
import com.example.freelance.model.Client;
import com.example.freelance.model.Competence;
import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.repository.MissionRepository;
import com.example.freelance.repository.ClientRepository;
import com.example.freelance.repository.CompetenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MissionService {


    private final MissionRepository missionRepository;
    private final ClientRepository clientRepository;
    private final CompetenceRepository competenceRepository;

    // Crée deux missions de test
    public void createTestMissions() {
        // Mission 1
        Mission mission1 = new Mission();
        mission1.setId(21507L);
        mission1.setTitre("Développement d'une application mobile");
        mission1.setDescription("Création d'une application mobile pour gérer les tâches.");
        mission1.setBudget(15000.00);
        mission1.setDuree("6 mois");
        mission1.setStatut(MissionStatut.EN_ATTENTE);

        // Récupération d'un client valide
        Client client1 = clientRepository.findById(351L).orElseThrow(() -> new RuntimeException("Client non trouvé"));
        mission1.setClient(client1);

        // Récupération des compétences valides
        Set<Competence> competences1 = new HashSet<>();
        Competence competence1 = competenceRepository.findById(2056L).orElseThrow(() -> new RuntimeException("Compétence non trouvée"));
        competences1.add(competence1);
        mission1.setCompetences(competences1);

        // Sauvegarde de la mission dans la base de données
        missionRepository.save(mission1);

        // Mission 2
        Mission mission2 = new Mission();
        mission2.setId(21509L);
        mission2.setTitre("Développement d'un site e-commerce");
        mission2.setDescription("Création d'un site de commerce en ligne pour une boutique.");
        mission2.setBudget(30000.00);
        mission2.setDuree("12 mois");
        mission2.setStatut(MissionStatut.EN_ATTENTE);

        // Récupération d'un autre client valide
        Client client2 = clientRepository.findById(352L).orElseThrow(() -> new RuntimeException("Client non trouvé"));
        mission2.setClient(client2);

        // Récupération des compétences valides
        Set<Competence> competences2 = new HashSet<>();
        Competence competence2 = competenceRepository.findById(2057L).orElseThrow(() -> new RuntimeException("Compétence non trouvée"));
        competences2.add(competence2);
        mission2.setCompetences(competences2);

        // Sauvegarde de la mission dans la base de données
        missionRepository.save(mission2);
    }
}

