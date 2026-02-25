package com.example.freelance.kafka;

import com.example.freelance.kafka.events.MissionCreatedEvent;
import com.example.freelance.service.NotificationV2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionEventConsumer {

    private final NotificationV2Service notificationV2Service;

    @KafkaListener(
            topics = "${app.kafka.topics.missionCreated}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onMissionCreated(MissionCreatedEvent event) {
        if (event == null || event.missionId() == null) {
            log.warn("[FLOW] Kafka CONSUME ignored null event={}", event);
            return;
        }

        MDC.put("traceId", String.valueOf(event.missionId()));

        try {
            log.info("[FLOW] Kafka CONSUME missionId={} createdAt={} source={}",
                    event.missionId(), event.createdAt(), event.source());

            int created = notificationV2Service.dispatchForMission(event.missionId());

            log.info("[FLOW] NOTIF DB inserted count={} missionId={}", created, event.missionId());
        } catch (Exception e) {
            log.error("[FLOW] NOTIF dispatch failed missionId={} event={}", event.missionId(), event, e);
        } finally {
            MDC.clear();
        }
    }
}