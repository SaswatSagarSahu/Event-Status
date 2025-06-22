package com.live.event.status.service;

import com.live.event.status.domain.EventScoreResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScoreApiClientTest {

    private WebClient.Builder builder;
    private WebClient webClient;
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;

    private ScoreApiClient scoreApiClient;

    @BeforeEach
    void setup() {
        builder = mock(WebClient.Builder.class);
        webClient = mock(WebClient.class);
        requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(builder.build()).thenReturn(webClient);

        scoreApiClient = new ScoreApiClient(builder);
    }

    @Test
    void testGetScore_Success() {
        String eventId = "match123";
        EventScoreResponse expected = EventScoreResponse.builder()
                .eventId(eventId)
                .currentScore("1-0")
                .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), Optional.ofNullable(any()))).thenReturn(requestHeadersSpec); // ✅ Fix
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(EventScoreResponse.class)).thenReturn(Mono.just(expected));

        scoreApiClient.baseUrl = "http://localhost:8080"; // ✅ Fix

        EventScoreResponse actual = scoreApiClient.getScore(eventId);

        assertNotNull(actual);
        assertEquals("match123", actual.getEventId());
        assertEquals("1-0", actual.getCurrentScore());
    }

    @Test
    void testGetScore_OnErrorReturnsNull() {
        String eventId = "match999";

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), Optional.ofNullable(any()))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(EventScoreResponse.class))
                .thenReturn(Mono.error(new RuntimeException("API failed")));

        EventScoreResponse response = scoreApiClient.getScore(eventId);

        assertNull(response);
    }
}
