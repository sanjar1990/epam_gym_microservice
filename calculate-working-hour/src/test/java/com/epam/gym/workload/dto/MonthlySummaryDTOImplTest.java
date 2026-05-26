package com.epam.gym.workload.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MonthlySummaryDTOImplTest {

    @Test
    void testGettersAndSetters() {
        // Arrange
        MonthlySummaryDTOImpl dto = new MonthlySummaryDTOImpl();

        String expectedUsername = "bruce_wayne";
        String expectedFirstName = "Bruce";
        String expectedLastName = "Wayne";
        Integer expectedYear = 2026;
        Integer expectedMonth = 5;
        Integer expectedDuration = 120;

        // Act
        dto.setTrainerUsername(expectedUsername);
        dto.setFirstName(expectedFirstName);
        dto.setLastName(expectedLastName);
        dto.setYear(expectedYear);
        dto.setMonth(expectedMonth);
        dto.setTotalDuration(expectedDuration);

        // Assert
        assertEquals(expectedUsername, dto.getTrainerUsername(), "Trainer username getter or setter failed");
        assertEquals(expectedFirstName, dto.getFirstName(), "First name getter or setter failed");
        assertEquals(expectedLastName, dto.getLastName(), "Last name getter or setter failed");
        assertEquals(expectedYear, dto.getYear(), "Year getter or setter failed");
        assertEquals(expectedMonth, dto.getMonth(), "Month getter or setter failed");
        assertEquals(expectedDuration, dto.getTotalDuration(), "Total duration getter or setter failed");
    }

    @Test
    void testNullValues() {
        // Arrange
        MonthlySummaryDTOImpl dto = new MonthlySummaryDTOImpl();

        // Act
        dto.setTrainerUsername(null);
        dto.setFirstName(null);
        dto.setLastName(null);
        dto.setYear(null);
        dto.setMonth(null);
        dto.setTotalDuration(null);

        // Assert
        assertNull(dto.getTrainerUsername());
        assertNull(dto.getFirstName());
        assertNull(dto.getLastName());
        assertNull(dto.getYear());
        assertNull(dto.getMonth());
        assertNull(dto.getTotalDuration());
    }
}