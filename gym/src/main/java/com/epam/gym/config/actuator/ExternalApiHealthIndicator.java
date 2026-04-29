package com.epam.gym.config.actuator;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class ExternalApiHealthIndicator implements HealthIndicator {

    private final RestTemplate restTemplate;

    public ExternalApiHealthIndicator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Health health() {
        try {
            ResponseEntity<String> response =
                    restTemplate.getForEntity("http://localhost:8080/api/v1/training-type", String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return Health.up()
                        .withDetail("externalApi", "Available")
                        .build();
            } else {
                return Health.down()
                        .withDetail("externalApi", "Bad response: " + response.getStatusCode())
                        .build();
            }

        } catch (Exception ex) {
            return Health.down()
                    .withDetail("externalApi", "Error: " + ex.getMessage())
                    .build();
        }
    }
}