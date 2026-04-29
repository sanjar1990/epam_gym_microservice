package com.epam.gym.config.actuator;

import com.epam.gym.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainingCapacityHealthIndicator implements HealthIndicator {
    private final TrainingService trainingService;


    @Override
    public Health health() {
        long activeTrainings = getActiveTrainings();

        if (activeTrainings < 100) {
            return Health.up()
                    .withDetail("activeTrainings", activeTrainings)
                    .build();
        } else {
            return Health.status("OVERLOADED")
                    .withDetail("activeTrainings", activeTrainings)
                    .build();
        }
    }

    private Long getActiveTrainings() {
        return trainingService.getTrainingsCount();
    }
}