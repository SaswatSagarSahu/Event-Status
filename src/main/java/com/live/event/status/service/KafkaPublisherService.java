package com.live.event.status.service;

import com.live.event.status.domain.EventScoreResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class KafkaPublisherService {

    private final KafkaTemplate<String, EventScoreResponse> kafkaTemplate;

    private static final String TOPIC = "event-scores";
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;

    public KafkaPublisherService(KafkaTemplate<String, EventScoreResponse> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishScore(EventScoreResponse response) {
        int attempt = 0;
        boolean success = false;

        while (attempt < MAX_RETRIES && !success) {
            try {
                CompletableFuture<SendResult<String, EventScoreResponse>> future =
                        kafkaTemplate.send(TOPIC, response.getEventId(), response);

                SendResult<String, EventScoreResponse> result = future.get();
                RecordMetadata metadata = result.getRecordMetadata();

                log.info("Successfully published to Kafka on attempt {}: topic={}, partition={}, offset={}",
                        attempt + 1, metadata.topic(), metadata.partition(), metadata.offset());

                success = true;
            } catch (Exception e) {
                attempt++;
                log.warn("Attempt {} to publish to Kafka failed: {}", attempt, e.getMessage());
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Retry sleep interrupted", ie);
                        break;
                    }
                } else {
                    log.error("All {} attempts to publish to Kafka failed", MAX_RETRIES, e);
                }
            }
        }
    }
}
