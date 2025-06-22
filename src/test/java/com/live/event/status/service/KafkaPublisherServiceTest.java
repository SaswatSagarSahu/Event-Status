package com.live.event.status.service;

import com.live.event.status.domain.EventScoreResponse;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class KafkaPublisherServiceTest {

    private KafkaTemplate<String, EventScoreResponse> kafkaTemplate;
    private KafkaPublisherService publisherService;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        publisherService = new KafkaPublisherService(kafkaTemplate);
    }

    @Test
    void testPublishScore_Success() throws Exception {
        EventScoreResponse response = EventScoreResponse.builder()
                .eventId("match123")
                .currentScore("2-0")
                .build();

        TopicPartition topicPartition = new TopicPartition("event-scores", 1);
        RecordMetadata metadata = new RecordMetadata(
                topicPartition,
                0L,
                1L,
                System.currentTimeMillis(),
                Long.valueOf(0),
                0, 0
        );

        SendResult<String, EventScoreResponse> sendResult = new SendResult<>(null, metadata);
        CompletableFuture<SendResult<String, EventScoreResponse>> future = CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        publisherService.publishScore(response);

        verify(kafkaTemplate, times(1)).send(eq("event-scores"), eq("match123"), eq(response));
    }

    @Test
    void testPublishScore_RetriesOnFailure() throws Exception {
        EventScoreResponse response = EventScoreResponse.builder()
                .eventId("match456")
                .currentScore("1-1")
                .build();

        CompletableFuture<SendResult<String, EventScoreResponse>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new ExecutionException("Kafka error", new Throwable()));

        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(failedFuture);

        publisherService.publishScore(response);

        verify(kafkaTemplate, times(3)).send(eq("event-scores"), eq("match456"), eq(response));
    }

    @Test
    void testPublishScore_InterruptedDuringRetrySleep() throws Exception {
        EventScoreResponse response = EventScoreResponse.builder()
                .eventId("match789")
                .currentScore("0-0")
                .build();

        CompletableFuture<SendResult<String, EventScoreResponse>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka exception"));

        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(failedFuture);

        Thread.currentThread().interrupt();

        publisherService.publishScore(response);

        verify(kafkaTemplate, atMost(3)).send(any(), any(), any());
        Thread.interrupted();
    }
}
