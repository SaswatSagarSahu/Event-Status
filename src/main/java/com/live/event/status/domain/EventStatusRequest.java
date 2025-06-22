package com.live.event.status.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventStatusRequest {

    private String eventId;
    private EventStatus eventStatus;
}
