package com.live.event.status.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InMemoryEventRegistryService {

    private final EventSchedulerService eventSchedulerService;
    private final Map<String, Boolean> eventStatusMap = new ConcurrentHashMap<>();

    @Autowired
    public InMemoryEventRegistryService(EventSchedulerService eventSchedulerService) {
        this.eventSchedulerService = eventSchedulerService;
    }

    public void updateEventStatus(String eventId, boolean isLive) {
        if (eventId == null || eventId.trim().isEmpty()) {
            throw new IllegalArgumentException("Event ID must not be null or empty.");
        }

        eventStatusMap.put(eventId, isLive);
        if (isLive) {
            eventSchedulerService.startEvent(eventId);
        } else {
            eventSchedulerService.stopEvent(eventId);
        }

        log.info("Event [{}] status updated to [{}]", eventId, isLive ? "LIVE" : "NOT LIVE");
    }

    public boolean isEventLive(String eventId) {
        return eventStatusMap.getOrDefault(eventId, false);
    }

    public Set<String> getAllLiveEvents() {
        return eventStatusMap.entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public void removeEvent(String eventId) {
        eventStatusMap.remove(eventId);
        eventSchedulerService.stopEvent(eventId);
        log.info("Event [{}] removed from registry", eventId);
    }

    public Map<String, Boolean> getAllEventStatuses() {
        return Collections.unmodifiableMap(eventStatusMap);
    }
}
