package com.live.event.status.service;

import com.live.event.status.domain.EventScoreResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class EventScoreManageService {

    private final Map<String, String> eventScoreMap = new ConcurrentHashMap<>();

    public void updateEventScore(String eventId, String score) {
        eventScoreMap.put(eventId, score);
        log.info("Event [{}] score updated to [{}]", eventId, score);
    }

    public EventScoreResponse getEventScore(String eventId) {
        return EventScoreResponse.builder()
                .eventId(eventId)
                .currentScore(eventScoreMap.get(eventId))
                .build();
    }
}
