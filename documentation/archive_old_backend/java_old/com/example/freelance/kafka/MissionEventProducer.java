package com.example.freelance.kafka;

import com.example.freelance.kafka.events.MissionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionEventProducer {

    private final KafkaTemplate<String, MissionCreatedEvent> kafkaTemplate;

    @Value("${app.kafka.topics.missionCreated}")
    private String topic;

    public void publishMissionCreated(Long missionId) {
        if (missionId == null) {
            log.warn("[FLOW] Kafka PRODUCE skipped (missionId=null)");
            return;
        }


        MDC.put("traceId", String.valueOf(missionId));

        MissionCreatedEvent event = new MissionCreatedEvent(missionId, Instant.now(), "mission-service");

        log.info("[FLOW] Kafka PRODUCE start topic={} key={}", topic, missionId);

        CompletableFuture<SendResult<String, MissionCreatedEvent>> future =
                kafkaTemplate.send(topic, String.valueOf(missionId), event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("[FLOW] Kafka PRODUCE failed topic={} key={} event={}", topic, missionId, event, ex);
                return;
            }

            var meta = result.getRecordMetadata();
            log.info("[FLOW] Kafka PRODUCE ok topic={} partition={} offset={} key={}",
                    meta.topic(), meta.partition(), meta.offset(), missionId);
        });
    }
}