package com.live.event.status.controller;

import com.live.event.status.domain.EventScoreRequest;
import com.live.event.status.domain.EventScoreResponse;
import com.live.event.status.service.EventScoreManageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventScoreControllerTest {

    @Mock
    private EventScoreManageService scoreService;

    @InjectMocks
    private EventScoreController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEventScore_Success() {
        String eventId = "match001";
        EventScoreResponse mockResponse = EventScoreResponse.builder()
                .eventId(eventId)
                .currentScore("3-2")
                .build();

        when(scoreService.getEventScore(eventId)).thenReturn(mockResponse);

        ResponseEntity<EventScoreResponse> response = controller.getEventScore(eventId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("3-2", response.getBody().getCurrentScore());
        verify(scoreService).getEventScore(eventId);
    }

    @Test
    void testUpdateEventScore_Success() {
        EventScoreRequest request = new EventScoreRequest("match002", "1-0");

        ResponseEntity<String> response = controller.updateEventScore(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Event score updated.", response.getBody());
        verify(scoreService).updateEventScore("match002", "1-0");
    }

    @Test
    void testUpdateEventScore_IllegalArgumentException() {
        EventScoreRequest request = new EventScoreRequest("match003", "invalid");

        doThrow(new IllegalArgumentException("Invalid score format"))
                .when(scoreService).updateEventScore("match003", "invalid");

        ResponseEntity<String> response = controller.updateEventScore(request);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Invalid score format"));
    }

    @Test
    void testUpdateEventScore_GenericException() {
        EventScoreRequest request = new EventScoreRequest("match004", "1-1");

        doThrow(new RuntimeException("Unexpected failure"))
                .when(scoreService).updateEventScore("match004", "1-1");

        ResponseEntity<String> response = controller.updateEventScore(request);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Internal Server Error"));
    }
}
