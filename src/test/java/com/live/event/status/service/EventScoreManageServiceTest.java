package com.live.event.status.service;

import com.live.event.status.domain.EventScoreResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventScoreManageServiceTest {

    private InMemoryEventRegistryService registryService;
    private EventScoreManageService scoreService;

    @BeforeEach
    void setUp() {
        registryService = mock(InMemoryEventRegistryService.class);
        scoreService = new EventScoreManageService(registryService);
    }

    @Test
    void testUpdateEventScore_Success() {
        String eventId = "event1";
        String score = "2-1";

        when(registryService.getEventStatus(eventId)).thenReturn(true);

        assertDoesNotThrow(() -> scoreService.updateEventScore(eventId, score));

        EventScoreResponse response = scoreService.getEventScore(eventId);
        assertEquals(eventId, response.getEventId());
        assertEquals(score, response.getCurrentScore());
    }

    @Test
    void testUpdateEventScore_EventIdNull_ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                scoreService.updateEventScore(null, "1-0")
        );
        assertEquals("Event ID must not be null or empty.", exception.getMessage());
    }

    @Test
    void testUpdateEventScore_EventIdEmpty_ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                scoreService.updateEventScore("   ", "1-0")
        );
        assertEquals("Event ID must not be null or empty.", exception.getMessage());
    }

    @Test
    void testUpdateEventScore_EventNotLive_ShouldThrowException() {
        String eventId = "inactive-event";
        when(registryService.getEventStatus(eventId)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                scoreService.updateEventScore(eventId, "1-0")
        );
        assertEquals("Event Is not active or live yet.", exception.getMessage());
    }

    @Test
    void testUpdateEventScore_ScoreIsNull_ShouldThrowException() {
        String eventId = "live-event";
        when(registryService.getEventStatus(eventId)).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                scoreService.updateEventScore(eventId, null)
        );
        assertEquals("Score must not be null.", exception.getMessage());
    }

    @Test
    void testGetAllEventScores() {
        String eventId = "event123";
        when(registryService.getEventStatus(eventId)).thenReturn(true);
        scoreService.updateEventScore(eventId, "3-3");

        Map<String, String> allScores = scoreService.getAllEventScores();

        assertEquals(1, allScores.size());
        assertEquals("3-3", allScores.get(eventId));
    }
}
