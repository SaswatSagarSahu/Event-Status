package com.live.event.status.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.*;

class InMemoryEventRegistryServiceTest {

    private EventSchedulerService eventSchedulerService;
    private InMemoryEventRegistryService registryService;

    @BeforeEach
    void setUp() {
        eventSchedulerService = mock(EventSchedulerService.class);
        registryService = new InMemoryEventRegistryService(eventSchedulerService);
    }

    @Test
    void testUpdateEventStatusToLive() {
        String eventId = "event1";

        registryService.updateEventStatus(eventId, true);

        Assertions.assertTrue(registryService.isEventLive(eventId));
        verify(eventSchedulerService).startEvent(eventId);
    }

    @Test
    void testUpdateEventStatusToNotLive() {
        String eventId = "event2";

        registryService.updateEventStatus(eventId, false);

        Assertions.assertFalse(registryService.isEventLive(eventId));
        verify(eventSchedulerService).stopEvent(eventId);
    }

    @Test
    void testGetAllLiveEvents() {
        registryService.updateEventStatus("event1", true);
        registryService.updateEventStatus("event2", false);
        registryService.updateEventStatus("event3", true);

        Set<String> liveEvents = registryService.getAllLiveEvents();

        Assertions.assertEquals(2, liveEvents.size());
        Assertions.assertTrue(liveEvents.contains("event1"));
        Assertions.assertTrue(liveEvents.contains("event3"));
        Assertions.assertFalse(liveEvents.contains("event2"));
    }

    @Test
    void testRemoveEvent() {
        String eventId = "event4";
        registryService.updateEventStatus(eventId, true);
        Assertions.assertTrue(registryService.isEventLive(eventId));

        registryService.removeEvent(eventId);

        Assertions.assertFalse(registryService.isEventLive(eventId));
        verify(eventSchedulerService).stopEvent(eventId);
    }

    @Test
    void testGetAllEventStatuses() {
        registryService.updateEventStatus("event1", true);
        registryService.updateEventStatus("event2", false);

        Map<String, Boolean> statuses = registryService.getAllEventStatuses();

        Assertions.assertEquals(2, statuses.size());
        Assertions.assertTrue(statuses.get("event1"));
        Assertions.assertFalse(statuses.get("event2"));
    }

    @Test
    void testUpdateEventStatusWithNullIdThrowsException() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            registryService.updateEventStatus(null, true);
        });
        Assertions.assertEquals("Event ID must not be null or empty.", exception.getMessage());
    }

    @Test
    void testUpdateEventStatusWithEmptyIdThrowsException() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            registryService.updateEventStatus("   ", false);
        });
        Assertions.assertEquals("Event ID must not be null or empty.", exception.getMessage());
    }

    @Test
    void testGetEventStatus() {
        String eventId = "event4";
        registryService.updateEventStatus(eventId, true);
        Assertions.assertTrue(registryService.getEventStatus(eventId));
    }
}
