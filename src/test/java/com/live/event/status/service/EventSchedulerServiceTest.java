package com.live.event.status.service;

import com.live.event.status.domain.EventScoreResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.mockito.Mockito.*;

class EventSchedulerServiceTest {

    private ScoreApiClient scoreApiClient;
    private KafkaPublisherService kafkaPublisherService;
    private EventSchedulerService schedulerService;

    @BeforeEach
    void setUp() {
        scoreApiClient = mock(ScoreApiClient.class);
        kafkaPublisherService = mock(KafkaPublisherService.class);
        schedulerService = new EventSchedulerService(scoreApiClient, kafkaPublisherService);
    }

    @Test
    void testStartEvent_SchedulesTask() throws InterruptedException {
        String eventId = "event123";
        EventScoreResponse mockResponse = EventScoreResponse.builder()
                .eventId(eventId)
                .currentScore("1-0")
                .build();

        when(scoreApiClient.getScore(eventId)).thenReturn(mockResponse);

        schedulerService.startEvent(eventId);

        TimeUnit.SECONDS.sleep(11);

        verify(scoreApiClient, atLeastOnce()).getScore(eventId);
        verify(kafkaPublisherService, atLeastOnce()).publishScore(mockResponse);
    }

    @Test
    void testStartEvent_DoesNotRescheduleIfAlreadyScheduled() throws InterruptedException {
        String eventId = "event456";
        EventScoreResponse mockResponse = EventScoreResponse.builder()
                .eventId(eventId)
                .currentScore("0-0")
                .build();

        when(scoreApiClient.getScore(eventId)).thenReturn(mockResponse);

        schedulerService.startEvent(eventId);
        schedulerService.startEvent(eventId);

        TimeUnit.SECONDS.sleep(1);

        verify(scoreApiClient, atMost(1)).getScore(eventId);
        verify(kafkaPublisherService, atMost(1)).publishScore(mockResponse);
    }

    @Test
    void testStopEvent_CancelsScheduledTask() throws InterruptedException {
        String eventId = "event789";
        EventScoreResponse mockResponse = EventScoreResponse.builder()
                .eventId(eventId)
                .currentScore("2-2")
                .build();

        when(scoreApiClient.getScore(eventId)).thenReturn(mockResponse);

        schedulerService.startEvent(eventId);
        TimeUnit.SECONDS.sleep(1);
        schedulerService.stopEvent(eventId);

        TimeUnit.SECONDS.sleep(12);

        verify(scoreApiClient, atMost(1)).getScore(eventId);
        verify(kafkaPublisherService, atMost(1)).publishScore(mockResponse);
    }

    @Test
    void testStopEvent_NoTaskToCancel() {
        String eventId = "nonExistingEvent";
        schedulerService.stopEvent(eventId);
    }
}
