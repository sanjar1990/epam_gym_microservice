package com.epam.gym.workload.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WorkloadTest {
    @Test
    void shouldSetAndGetAllFields() {

        Workload workload = new Workload();

        String id = "1";
        String username = "john";
        String firstName = "John";
        String lastName = "Doe";
        Boolean status = true;

        YearSummary yearSummary = YearSummary.builder()
                .year(2025)
                .months(List.of())
                .build();

        List<YearSummary> years = List.of(yearSummary);

        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime updatedOn = LocalDateTime.now().plusHours(1);

        workload.setId(id);
        workload.setTrainerUsername(username);
        workload.setFirstName(firstName);
        workload.setLastName(lastName);
        workload.setStatus(status);
        workload.setYears(years);
        workload.setCreatedOn(createdOn);
        workload.setUpdatedOn(updatedOn);

        assertEquals(id, workload.getId());
        assertEquals(username, workload.getTrainerUsername());
        assertEquals(firstName, workload.getFirstName());
        assertEquals(lastName, workload.getLastName());
        assertEquals(status, workload.getStatus());
        assertEquals(years, workload.getYears());
        assertEquals(createdOn, workload.getCreatedOn());
        assertEquals(updatedOn, workload.getUpdatedOn());
    }

    @Test
    void shouldInitializeCreatedOnAndUpdatedOn() {

        Workload workload = new Workload();

        assertNotNull(workload.getCreatedOn());
        assertNotNull(workload.getUpdatedOn());
    }

    @Test
    void shouldAllowNullValues() {

        Workload workload = new Workload();

        workload.setId(null);
        workload.setTrainerUsername(null);
        workload.setFirstName(null);
        workload.setLastName(null);
        workload.setStatus(null);
        workload.setYears(null);
        workload.setCreatedOn(null);
        workload.setUpdatedOn(null);

        assertNull(workload.getId());
        assertNull(workload.getTrainerUsername());
        assertNull(workload.getFirstName());
        assertNull(workload.getLastName());
        assertNull(workload.getStatus());
        assertNull(workload.getYears());
        assertNull(workload.getCreatedOn());
        assertNull(workload.getUpdatedOn());
    }

    @Test
    void shouldUpdateFieldsCorrectly() {

        Workload workload = new Workload();

        workload.setTrainerUsername("john");
        assertEquals("john", workload.getTrainerUsername());

        workload.setTrainerUsername("mike");
        assertEquals("mike", workload.getTrainerUsername());
    }
}