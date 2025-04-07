package com.example.freelance.service;

import com.example.freelance.model.*;
import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.repository.MissionRepository;
import com.example.freelance.repository.ClientRepository;
import com.example.freelance.repository.CompetenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final ClientRepository clientRepository;
    private final CompetenceRepository competenceRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FreelanceService freelanceService;

//
//    public void createTestMissions() {
//        // Mission 1
//        Mission mission1 = new Mission();
//        mission1.setTitre("Développement d'une application mobile");
//        mission1.setDescription("Demo avec prof de code .");
//        mission1.setBudget(15000.00);
//        mission1.setDuree("3 mois");
//        mission1.setStatut(MissionStatut.EN_ATTENTE);
//
//
//        Client client1 = clientRepository.findById(351L).orElseThrow(() -> new RuntimeException("Client non trouvé"));
//        mission1.setClient(client1);
//
//
//        Set<Competence> competences1 = new HashSet<>();
//        Competence competence1 = competenceRepository.findById(2056L).orElseThrow(() -> new RuntimeException("Compétence non trouvée"));
//        competences1.add(competence1);
//        mission1.setCompetences(competences1);
//
//
//        missionRepository.save(mission1);
//
//        // Mission 2
//        Mission mission2 = new Mission();
//        mission2.setTitre("Développement d'un site e-commerce");
//        mission2.setDescription("Demo avec le prof .");
//        mission2.setBudget(30000.00);
//        mission2.setDuree("9 mois");
//        mission2.setStatut(MissionStatut.EN_ATTENTE);
//
//
//        Client client2 = clientRepository.findById(352L).orElseThrow(() -> new RuntimeException("Client non trouvé"));
//        mission2.setClient(client2);
//
//
//        Set<Competence> competences2 = new HashSet<>();
//        Competence competence2 = competenceRepository.findById(2057L).orElseThrow(() -> new RuntimeException("Compétence non trouvée"));
//        competences2.add(competence2);
//        mission2.setCompetences(competences2);
//
//
//        missionRepository.save(mission2);
//    }
    public Mission saveMission(Mission m) {
        Mission mission = missionRepository.save(m);
        List<Freelancer> availableFreelancers = freelanceService.getAllAvailableFreelancers();

        availableFreelancers.forEach(freelancer -> {
            Notification notification = new Notification("A new mission has been added, check it now !!", freelancer, mission);
            notificationService.saveNotification(notification);
        });

        return mission;
    }
    public Mission updateMissionStatus(Long missionId, MissionStatut newStatut) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission non trouvée"));


        if (!isValidStatusTransition(mission.getStatut(), newStatut)) {
            throw new IllegalStateException("Changement de statut non autorisé");
        }

        mission.setStatut(newStatut);
        return missionRepository.save(mission);
    }
    private boolean isValidStatusTransition(MissionStatut current, MissionStatut next) {
        return switch (current) {
            case EN_ATTENTE -> next == MissionStatut.EN_NEGOCIATION;
            case EN_NEGOCIATION -> next == MissionStatut.ANNULEE || next == MissionStatut.ACCEPTEE;
            case ACCEPTEE -> next == MissionStatut.TERMINEE;
            case TERMINEE -> false;
            default -> false;
        };
    }
    public List<Mission> getMonitoredMissions() {
        return missionRepository.findByIdBetween(39970L,40045L);
    }

}
