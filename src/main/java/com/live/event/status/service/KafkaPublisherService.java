package com.live.event.status.service;

import com.live.event.status.domain.EventScoreResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPublisherService {

    private final KafkaTemplate<String, EventScoreResponse> kafkaTemplate;

    private static final String TOPIC = "event-scores";

    public void publishScore(EventScoreResponse response) {
        try {
            kafkaTemplate.send(TOPIC, response.getEventId(), response)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Successfully published: {}", response);
                        } else {
                            log.error("Failed to publish to Kafka: {}", ex.getMessage(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Kafka publish exception: {}", e.getMessage(), e);
        }
    }

}
