package com.epam.gym.config.actuator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.health.contributor.Health;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ExternalApiHealthIndicatorTest {

    @Mock
    private RestTemplate restTemplate;

    private ExternalApiHealthIndicator healthIndicator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        healthIndicator = new ExternalApiHealthIndicator(restTemplate);
    }

    @Test
    void health_shouldReturnUp_whenApiIsAvailable() {
        ResponseEntity<String> response =
                new ResponseEntity<>("OK", HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(response);

        Health health = healthIndicator.health();

        assertEquals("UP", health.getStatus().getCode());
        assertEquals("Available", health.getDetails().get("externalApi"));
    }

    @Test
    void health_shouldReturnDown_whenApiReturnsErrorStatus() {
        ResponseEntity<String> response =
                new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);

        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenReturn(response);

        Health health = healthIndicator.health();

        assertEquals("DOWN", health.getStatus().getCode());
        assertTrue(health.getDetails().get("externalApi")
                .toString()
                .contains("Bad response"));
    }

    @Test
    void health_shouldReturnDown_whenExceptionOccurs() {
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection failed"));

        Health health = healthIndicator.health();

        assertEquals("DOWN", health.getStatus().getCode());
        assertTrue(health.getDetails().get("externalApi")
                .toString()
                .contains("Error"));
    }
}