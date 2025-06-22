package com.live.event.status.controller;

import com.live.event.status.domain.EventStatus;
import com.live.event.status.domain.EventStatusRequest;
import com.live.event.status.service.InMemoryEventRegistryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/events")
public class EventRegistryController {

    private final InMemoryEventRegistryService eventRegistryService;

    public EventRegistryController(InMemoryEventRegistryService eventRegistryService) {
        this.eventRegistryService = eventRegistryService;
    }

    @PostMapping("/update-status")
    public ResponseEntity<String> updateEventStatus(@RequestBody EventStatusRequest request) {
        eventRegistryService.updateEventStatus(request.getEventId(), EventStatus.LIVE.equals(request.getEventStatus()));
        return ResponseEntity.ok("Event status updated.");
    }

    @GetMapping("/is-live/{eventId}")
    public ResponseEntity<Boolean> isEventLive(@PathVariable String eventId) {
        return ResponseEntity.ok(eventRegistryService.isEventLive(eventId));
    }

    @GetMapping("/live")
    public ResponseEntity<Set<String>> getAllLiveEvents() {
        return ResponseEntity.ok(eventRegistryService.getAllLiveEvents());
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<String> removeEvent(@PathVariable String eventId) {
        eventRegistryService.removeEvent(eventId);
        return ResponseEntity.ok("Event removed from registry.");
    }

    @GetMapping("/all-statuses")
    public ResponseEntity<Map<String, Boolean>> getAllEventStatuses() {
        return ResponseEntity.ok(eventRegistryService.getAllEventStatuses());
    }

}
