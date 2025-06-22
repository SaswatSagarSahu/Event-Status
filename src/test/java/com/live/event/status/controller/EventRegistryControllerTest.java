package com.live.event.status.controller;

import com.live.event.status.domain.EventStatus;
import com.live.event.status.domain.EventStatusRequest;
import com.live.event.status.service.InMemoryEventRegistryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventRegistryControllerTest {

    @Mock
    private InMemoryEventRegistryService eventRegistryService;

    @InjectMocks
    private EventRegistryController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateEventStatus_Live() {
        EventStatusRequest request = new EventStatusRequest("event123", EventStatus.LIVE);

        ResponseEntity<String> response = controller.updateEventStatus(request);

        verify(eventRegistryService).updateEventStatus("event123", true);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Event status updated.", response.getBody());
    }

    @Test
    void testUpdateEventStatus_NotLive() {
        EventStatusRequest request = new EventStatusRequest("event456", EventStatus.NOTLIVE);

        ResponseEntity<String> response = controller.updateEventStatus(request);

        verify(eventRegistryService).updateEventStatus("event456", false);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testIsEventLive() {
        when(eventRegistryService.isEventLive("event123")).thenReturn(true);

        ResponseEntity<Boolean> response = controller.isEventLive("event123");

        verify(eventRegistryService).isEventLive("event123");
        assertTrue(response.getBody());
    }

    @Test
    void testGetAllLiveEvents() {
        Set<String> mockLiveEvents = Set.of("event1", "event2");
        when(eventRegistryService.getAllLiveEvents()).thenReturn(mockLiveEvents);

        ResponseEntity<Set<String>> response = controller.getAllLiveEvents();

        verify(eventRegistryService).getAllLiveEvents();
        assertEquals(mockLiveEvents, response.getBody());
    }

    @Test
    void testRemoveEvent() {
        ResponseEntity<String> response = controller.removeEvent("event789");

        verify(eventRegistryService).removeEvent("event789");
        assertEquals("Event removed from registry.", response.getBody());
    }

    @Test
    void testGetAllEventStatuses() {
        Map<String, Boolean> statuses = Map.of("event1", true, "event2", false);
        when(eventRegistryService.getAllEventStatuses()).thenReturn(statuses);

        ResponseEntity<Map<String, Boolean>> response = controller.getAllEventStatuses();

        verify(eventRegistryService).getAllEventStatuses();
        assertEquals(statuses, response.getBody());
    }
}
