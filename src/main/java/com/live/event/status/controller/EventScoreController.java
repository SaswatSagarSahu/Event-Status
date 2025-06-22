package com.live.event.status.controller;

import com.live.event.status.domain.BaseResponse;
import com.live.event.status.service.EventScoreManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class EventScoreController {

    @Autowired
    private EventScoreManageService eventScoreManageService;

    @GetMapping("/events/{eventId}/score")
    public ResponseEntity<?> getEventScore(@PathVariable String eventId){
        return ResponseEntity.ok(eventScoreManageService.getEventScore(eventId));
    }

    @PostMapping("/events/{eventId}/score/{score}")
    public ResponseEntity<?> addOrUpdateEventScore(@PathVariable String eventId, @PathVariable String score){
        eventScoreManageService.updateEventScore(eventId, score);
        return new ResponseEntity<>(new BaseResponse(true, "Created"), HttpStatus.CREATED);
    }

}
