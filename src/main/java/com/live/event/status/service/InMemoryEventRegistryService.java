package com.live.event.status.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory registry service for managing live status of events.
 * This service is thread-safe and is used as a temporary store for event states.
 */
@Slf4j
@Service
public class InMemoryEventRegistryService {

    private final EventSchedulerService eventSchedulerService;
    private final Map<String, Boolean> eventStatusMap = new ConcurrentHashMap<>();

    public InMemoryEventRegistryService(EventSchedulerService eventSchedulerService) {
        this.eventSchedulerService = eventSchedulerService;
    }

    /**
     * Updates the live status of an event.
     * Triggers the start or stop of the event using EventSchedulerService.
     *
     * @param eventId the unique identifier for the event
     * @param isLive  true if event is live, false otherwise
     */
    public void updateEventStatus(String eventId, boolean isLive) {
        validateEventId(eventId);

        eventStatusMap.put(eventId, isLive);

        try {
            if (isLive) {
                eventSchedulerService.startEvent(eventId);
            } else {
                eventSchedulerService.stopEvent(eventId);
            }
            log.info("Event [{}] status updated to [{}]", eventId, isLive ? "LIVE" : "NOT LIVE");
        } catch (Exception e) {
            log.error("Failed to process event [{}] status change: {}", eventId, e.getMessage(), e);
            throw new RuntimeException("Event scheduling failed for event: " + eventId, e);
        }
    }

    /**
     * Returns whether the specified event is currently live.
     *
     * @param eventId the unique identifier for the event
     * @return true if live, false otherwise
     */
    public boolean isEventLive(String eventId) {
        validateEventId(eventId);
        return eventStatusMap.getOrDefault(eventId, false);
    }

    /**
     * Returns a set of all event IDs that are currently live.
     *
     * @return set of live event IDs
     */
    public Set<String> getAllLiveEvents() {
        return eventStatusMap.entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Removes an event from the registry and stops it.
     *
     * @param eventId the unique identifier for the event
     */
    public void removeEvent(String eventId) {
        validateEventId(eventId);

        if (eventStatusMap.remove(eventId) != null) {
            try {
                eventSchedulerService.stopEvent(eventId);
                log.info("Event [{}] removed from registry", eventId);
            } catch (Exception e) {
                log.error("Failed to stop event [{}] during removal: {}", eventId, e.getMessage(), e);
            }
        } else {
            log.warn("Attempted to remove non-existent event [{}]", eventId);
        }
    }

    /**
     * Returns an unmodifiable view of all event statuses.
     *
     * @return map of event IDs and their live status
     */
    public Map<String, Boolean> getAllEventStatuses() {
        return Collections.unmodifiableMap(eventStatusMap);
    }

    /**
     * Returns an status of one event Id.
     *
     * @return Boolean of event IDs status
     */
    public Boolean getEventStatus(String eventId) {
        return eventStatusMap.getOrDefault(eventId, false);
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
    }
}
