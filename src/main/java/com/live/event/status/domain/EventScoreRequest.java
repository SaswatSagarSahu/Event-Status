package com.live.event.status.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventScoreRequest {

    @NotBlank(message = "eventId is mandatory")
    private String eventId;

    @NotBlank(message = "score is mandatory")
    private String score;
}
