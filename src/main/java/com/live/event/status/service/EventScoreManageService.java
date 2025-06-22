package com.live.event.status.service;

import com.live.event.status.domain.EventScoreResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service to manage event scores in memory.
 * Stores and retrieves scores for live events.
 */
@Slf4j
@Service
public class EventScoreManageService {

    private final Map<String, String> eventScoreMap = new ConcurrentHashMap<>();

    private final InMemoryEventRegistryService inMemoryEventRegistryService;

    @Autowired
    public EventScoreManageService(InMemoryEventRegistryService inMemoryEventRegistryService) {
        this.inMemoryEventRegistryService = inMemoryEventRegistryService;
    }

    /**
     * Updates the score for a given event.
     *
     * @param eventId the ID of the event
     * @param score   the updated score
     */
    public void updateEventScore(String eventId, String score) {
        validateEventId(eventId);
        if (score == null) {
            throw new IllegalArgumentException("Score must not be null.");
        }

        eventScoreMap.put(eventId, score);
        log.info("Event [{}] score updated to [{}]", eventId, score);
    }

    /**
     * Retrieves the current score for a given event.
     *
     * @param eventId the ID of the event
     * @return EventScoreResponse containing event ID and current score
     */
    public EventScoreResponse getEventScore(String eventId) {
        validateEventId(eventId);

        String score = eventScoreMap.get(eventId);
        if (score == null) {
            log.warn("No score found for event [{}]", eventId);
        }

        return EventScoreResponse.builder()
                .eventId(eventId)
                .currentScore(score)
                .build();
    }

    /**
     * Returns an unmodifiable map of all event scores.
     * Useful for monitoring or admin APIs.
     *
     * @return unmodifiable map of eventId → score
     */
    public Map<String, String> getAllEventScores() {
        return Collections.unmodifiableMap(eventScoreMap);
    }

    /**
     * Validates the event ID.
     *
     * @param eventId the event ID to validate
     */
    private void validateEventId(String eventId) {
        if (eventId == null || eventId.trim().isEmpty()) {
            throw new IllegalArgumentException("Event ID must not be null or empty.");
        }
        if(!inMemoryEventRegistryService.getEventStatus(eventId)){
            throw new IllegalArgumentException("Event Is not active or live yet.");
        }
    }
}
