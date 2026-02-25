package com.example.freelance.service;

import com.example.freelance.kafka.MissionEventProducer;
import com.example.freelance.model.Competence;
import com.example.freelance.model.Mission;
import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.repository.CompetenceRepository;
import com.example.freelance.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final CompetenceRepository competenceRepository;
    private final MissionEventProducer missionEventProducer;

    @Transactional
    public Mission saveMission(Mission m) {
        Set<String> requestedNames = extractCompetenceNames(m.getCompetences());

        log.info("[FLOW] Mission prepare competences requested={}", requestedNames);

        if (requestedNames.isEmpty()) {
            m.setCompetences(new HashSet<>());
        } else {
            Set<Competence> persistentCompetences = new HashSet<>();

            for (String name : requestedNames) {
                Competence existing = findAnyCompetenceByName(name);

                if (existing != null) {
                    persistentCompetences.add(existing);
                } else {
                    Competence created = new Competence();
                    created.setNom(name);
                    created.setDescription(name + " (auto)");
                    created.setFreelancer(null);
                    Competence savedComp = competenceRepository.save(created);
                    persistentCompetences.add(savedComp);
                    log.info("[FLOW] Competence created name='{}' id={}", savedComp.getNom(), savedComp.getId());
                }
            }

            m.setCompetences(persistentCompetences);
        }

        // Sauvegarde mission
        Mission mission = missionRepository.save(m);

        // maintenant on a missionId => on bascule le traceId dessus
        MDC.put("traceId", String.valueOf(mission.getId()));
        log.info("[FLOW] DB mission saved missionId={} titre='{}'", mission.getId(), safe(mission.getTitre()));

        // Kafka publish
        missionEventProducer.publishMissionCreated(mission.getId());

        return mission;
    }

    private Set<String> extractCompetenceNames(Set<Competence> competences) {
        if (competences == null || competences.isEmpty()) return Collections.emptySet();

        Set<String> names = new HashSet<>();
        for (Competence c : competences) {
            if (c == null) continue;
            if (c.getNom() != null && !c.getNom().isBlank()) {
                names.add(c.getNom().trim());
            }
        }
        return names;
    }

    private Competence findAnyCompetenceByName(String name) {
        if (name == null) return null;
        String target = name.trim().toLowerCase();

        List<Competence> all = competenceRepository.findAll();
        for (Competence c : all) {
            if (c == null || c.getNom() == null) continue;
            if (c.getNom().trim().toLowerCase().equals(target)) {
                return c;
            }
        }
        return null;
    }

    public Mission updateMissionStatus(Long missionId, MissionStatut newStatut) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission non trouvée"));

        mission.setStatut(newStatut);
        return missionRepository.save(mission);
    }

    public List<Mission> getMonitoredMissions() {
        return missionRepository.findByIdBetween(42194L, 42239L);
    }

    private String safe(String s) {
        return s == null ? null : s.replaceAll("\\s+", " ").trim();
    }
}