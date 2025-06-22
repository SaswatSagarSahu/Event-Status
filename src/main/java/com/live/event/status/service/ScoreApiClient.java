package com.live.event.status.service;

import com.live.event.status.domain.EventScoreResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScoreApiClient {

    private final WebClient webClient = WebClient.create();

    @Value("${external.api.base-url}")
    private String baseUrl;

    public EventScoreResponse getScore(String eventId) {
        try {
            return webClient.get()
                    .uri(baseUrl + "/api/v1/events/{eventId}/score", eventId)
                    .retrieve()
                    .bodyToMono(EventScoreResponse.class)
                    .onErrorResume(e -> {
                        log.error("Failed to fetch score for event {}: {}", eventId, e.getMessage());
                        return Mono.empty();
                    })
                    .block();
        } catch (Exception e) {
            log.error("Exception while calling score API: {}", e.getMessage(), e);
            return null;
        }
    }
}
