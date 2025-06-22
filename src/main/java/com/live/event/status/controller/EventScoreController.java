package com.live.event.status.controller;

import com.live.event.status.domain.EventScoreRequest;
import com.live.event.status.domain.EventScoreResponse;
import com.live.event.status.service.EventScoreManageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/scores")
public class EventScoreController {

    private final EventScoreManageService scoreService;

    public EventScoreController(EventScoreManageService scoreService) {
        this.scoreService = scoreService;
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventScoreResponse> getEventScore(@PathVariable String eventId) {
        EventScoreResponse response = scoreService.getEventScore(eventId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateEventScore(@RequestBody EventScoreRequest request) {
        try {
            scoreService.updateEventScore(request.getEventId(), request.getScore());
            return ResponseEntity.ok("Event score updated.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Internal Server Error: " + e.getMessage());
        }
    }


}
