package com.live.event.status.service;

import com.live.event.status.domain.EventScoreResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Service
public class EventSchedulerService {

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    private final ScoreApiClient scoreApiClient;
    private final KafkaPublisherService kafkaPublisherService;

    @Autowired
    public EventSchedulerService(ScoreApiClient scoreApiClient, KafkaPublisherService kafkaPublisherService) {
        this.scoreApiClient = scoreApiClient;
        this.kafkaPublisherService = kafkaPublisherService;
    }

    public void startEvent(String eventId) {
        if (scheduledTasks.containsKey(eventId)) {
            log.info("Event {} is already scheduled.", eventId);
            return;
        }

        ScheduledFuture<?> task = executorService.scheduleAtFixedRate(() -> {
            try {
                log.info("Fetching score for event {}", eventId);
                EventScoreResponse response = scoreApiClient.getScore(eventId);
                kafkaPublisherService.publishScore(response);
                log.info("Published score: {}", response);
            } catch (Exception e) {
                log.error("Error fetching or publishing score for event {}: {}", eventId, e.getMessage(), e);
            }
        }, 0, 10, TimeUnit.SECONDS);

        scheduledTasks.put(eventId, task);
        log.info("Started scheduled task for event {}", eventId);
    }

    public void stopEvent(String eventId) {
        ScheduledFuture<?> task = scheduledTasks.remove(eventId);
        if (task != null) {
            task.cancel(true);
            log.info("Stopped scheduled task for event {}", eventId);
        } else {
            log.info("No scheduled task found for event {}", eventId);
        }
    }
}
