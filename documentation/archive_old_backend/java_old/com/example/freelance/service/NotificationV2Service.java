package com.example.freelance.service;

import com.example.freelance.dto.NotificationDto;
import com.example.freelance.model.Competence;
import com.example.freelance.model.Freelancer;
import com.example.freelance.model.Mission;
import com.example.freelance.model.Notification;
import com.example.freelance.repository.FreelancerRepository;
import com.example.freelance.repository.MissionRepository;
import com.example.freelance.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationV2Service {

    private final NotificationRepository notificationRepository;
    private final MissionRepository missionRepository;
    private final FreelancerRepository freelancerRepository;
    private final NotificationWsService notificationWsService;

    public List<NotificationDto> getByFreelancer(Long freelancerId) {
        if (freelancerId == null) return List.of();
        return notificationRepository.findByFreelancerIdOrderByTimestampDesc(freelancerId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public long unreadCount(Long freelancerId) {
        if (freelancerId == null) return 0;
        return notificationRepository.countUnreadByFreelancerId(freelancerId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        if (notificationId == null) {
            log.warn("[FLOW] NOTIF markAsRead ignored (notificationId=null)");
            return;
        }

        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));

        // traceId = missionId si possible, sinon notifId
        if (n.getMission() != null && n.getMission().getId() != null) {
            MDC.put("traceId", String.valueOf(n.getMission().getId()));
        } else {
            MDC.put("traceId", "notif-" + notificationId);
        }

        try {
            if (n.isRead()) {
                log.info("[FLOW] NOTIF markAsRead skipped (already read) notificationId={}", notificationId);
                return;
            }

            n.setRead(true);
            notificationRepository.save(n);

            log.info("[FLOW] NOTIF markAsRead ok notificationId={} freelancerId={} missionId={}",
                    n.getId(),
                    n.getFreelancer() != null ? n.getFreelancer().getId() : null,
                    n.getMission() != null ? n.getMission().getId() : null
            );

            // Push WS pour mise à jour UI côté freelance
            if (n.getFreelancer() != null && n.getFreelancer().getId() != null) {
                NotificationDto dto = toDto(n);
                notificationWsService.pushToFreelancer(n.getFreelancer().getId(), dto);
                log.info("[FLOW] NOTIF WS pushed (read update) freelancerId={} notificationId={}",
                        n.getFreelancer().getId(), n.getId());
            }
        } finally {
            MDC.clear();
        }
    }

    @Transactional
    public int dispatchForMission(Long missionId) {
        if (missionId == null) {
            log.warn("[FLOW] NOTIF dispatch ignored (missionId=null)");
            return 0;
        }

        MDC.put("traceId", String.valueOf(missionId));

        try {
            Mission mission = missionRepository.findWithCompetencesById(missionId)
                    .orElseThrow(() -> new RuntimeException("Mission not found: " + missionId));

            // 1) Collect competences mission
            Set<String> missionCompetenceNames = new LinkedHashSet<>();
            if (mission.getCompetences() != null) {
                for (Competence c : mission.getCompetences()) {
                    if (c != null && c.getNom() != null && !c.getNom().isBlank()) {
                        missionCompetenceNames.add(normalize(c.getNom()));
                    }
                }
            }

            log.info("[FLOW] NOTIF match start missionId={} titre='{}' competences={}",
                    missionId, safe(mission.getTitre()), missionCompetenceNames);

            if (missionCompetenceNames.isEmpty()) {
                log.info("[FLOW] NOTIF match stop (no competences) missionId={}", missionId);
                return 0;
            }

            // 2) Find targets (available freelancers with at least 1 competence)
            Map<Long, Freelancer> targets = new LinkedHashMap<>();
            for (String compName : missionCompetenceNames) {
                List<Freelancer> list = freelancerRepository.findAvailableByCompetenceName(compName);
                log.info("[FLOW] NOTIF match query competence='{}' -> {} freelancers", compName, list != null ? list.size() : 0);

                if (list == null) continue;

                for (Freelancer f : list) {
                    if (f != null && f.getId() != null) {
                        targets.putIfAbsent(f.getId(), f);
                    }
                }
            }

            log.info("[FLOW] NOTIF match targets={} missionId={}", targets.size(), missionId);

            if (targets.isEmpty()) {
                log.info("[FLOW] NOTIF match stop (no targets) missionId={}", missionId);
                return 0;
            }

            // 3) Create notifications
            LocalDateTime now = LocalDateTime.now();
            List<Notification> notifs = new ArrayList<>(targets.size());

            for (Freelancer f : targets.values()) {
                Notification n = new Notification();
                n.setMessage("Nouvelle mission disponible: " + mission.getTitre());
                n.setTimestamp(now);
                n.setRead(false);
                n.setFreelancer(f);
                n.setMission(mission);
                notifs.add(n);
            }

            List<Notification> saved = notificationRepository.saveAll(notifs);
            log.info("[FLOW] NOTIF DB inserted count={} missionId={}", saved.size(), missionId);

            // 4) Push websocket notifications
            int pushed = 0;
            for (Notification n : saved) {
                if (n.getFreelancer() != null && n.getFreelancer().getId() != null) {
                    notificationWsService.pushToFreelancer(n.getFreelancer().getId(), toDto(n));
                    pushed++;
                }
            }
            log.info("[FLOW] NOTIF WS pushed={} missionId={}", pushed, missionId);

            return saved.size();

        } catch (Exception e) {
            log.error("[FLOW] NOTIF dispatch failed missionId={}", missionId, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    private String normalize(String s) {
        return s == null ? null : s.trim().toLowerCase();
    }

    private String safe(String s) {
        return s == null ? null : s.replaceAll("\\s+", " ").trim();
    }

    private NotificationDto toDto(Notification n) {
        return new NotificationDto(
                n.getId(),
                n.getMessage(),
                n.getTimestamp(),
                n.isRead(),
                n.getFreelancer() != null ? n.getFreelancer().getId() : null,
                n.getMission() != null ? n.getMission().getId() : null,
                n.getMission() != null ? n.getMission().getTitre() : null
        );
    }
}