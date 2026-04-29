package com.epam.gym.config.actuator;

import com.epam.gym.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.Health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TrainingCapacityHealthIndicatorTest {

    private final TrainingService trainingService = mock(TrainingService.class);

    private final TrainingCapacityHealthIndicator healthIndicator =
            new TrainingCapacityHealthIndicator(trainingService);

    @Test
    void health_shouldBeUp_whenActiveTrainingsLessThan100() {
        // given
        when(trainingService.getTrainingsCount()).thenReturn(50L);

        // when
        Health health = healthIndicator.health();

        // then
        assertEquals("UP", health.getStatus().getCode());
        assertEquals(50L, health.getDetails().get("activeTrainings"));
    }

    @Test
    void health_shouldBeOverloaded_whenActiveTrainingsMoreOrEqual100() {
        // given
        when(trainingService.getTrainingsCount()).thenReturn(150L);

        // when
        Health health = healthIndicator.health();

        // then
        assertEquals("OVERLOADED", health.getStatus().getCode());
        assertEquals(150L, health.getDetails().get("activeTrainings"));
    }
}