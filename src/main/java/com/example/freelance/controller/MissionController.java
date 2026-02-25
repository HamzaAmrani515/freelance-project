package com.example.freelance.controller;

import com.example.freelance.model.Mission;
import com.example.freelance.model.enums.MissionStatut;
import com.example.freelance.service.FreelanceService;
import com.example.freelance.service.MissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions")
public class MissionController {

    @Autowired
    private FreelanceService freelanceService;

    private final MissionService missionService;

    @PostMapping("/post")
    public Mission postMachine(@RequestBody Mission mission) {
        // traceId court pour la lisibilité en démo
        MDC.put("traceId", UUID.randomUUID().toString().substring(0, 8));

        try {
            log.info("[FLOW] HTTP POST /api/missions/post titre='{}' budget={} statut={} clientId={}",
                    safe(mission.getTitre()),
                    mission.getBudget(),
                    mission.getStatut(),
                    mission.getClient() != null ? mission.getClient().getId() : null
            );

            Mission saved = missionService.saveMission(mission);

            log.info("[FLOW] HTTP 200 mission saved missionId={} titre='{}'",
                    saved.getId(),
                    safe(saved.getTitre())
            );

            return saved;
        } finally {
            MDC.clear();
        }
    }

    @PutMapping("/put/{id}")
    public Mission putMachine(@PathVariable long id,
                              @RequestParam(name = "status", required = true) MissionStatut status) {
        MDC.put("traceId", String.valueOf(id));
        try {
            log.info("[FLOW] HTTP PUT /api/missions/put/{} status={}", id, status);
            Mission updated = missionService.updateMissionStatus(id, status);
            log.info("[FLOW] HTTP 200 mission status updated missionId={} newStatus={}", id, status);
            return updated;
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/monitored")
    public List<Mission> getMonitoredMissions() {
        log.info("[FLOW] HTTP GET /api/missions/monitored");
        return missionService.getMonitoredMissions();
    }

    private String safe(String s) {
        return s == null ? null : s.replaceAll("\\s+", " ").trim();
    }
}