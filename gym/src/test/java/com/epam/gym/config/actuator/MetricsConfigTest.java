package com.epam.gym.config.actuator;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class MetricsConfigTest {

    private final MetricsConfig metricsConfig = new MetricsConfig();

    @Test
    void timedAspect_shouldCreateBean() {
        MeterRegistry registry = mock(MeterRegistry.class);

        TimedAspect result = metricsConfig.timedAspect(registry);

        assertNotNull(result);
    }

    @Test
    void countedAspect_shouldCreateBean() {
        MeterRegistry registry = mock(MeterRegistry.class);

        CountedAspect result = metricsConfig.countedAspect(registry);

        assertNotNull(result);
    }
}