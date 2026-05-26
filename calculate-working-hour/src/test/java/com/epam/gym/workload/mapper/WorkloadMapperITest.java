package com.epam.gym.workload.mapper;

import com.epam.gym.workload.dto.TrainerWorkloadRequest;
import com.epam.gym.workload.entity.Workload;
import com.epam.gym.workload.enums.ActionType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class WorkloadMapperITest {

    private final WorkloadMapperI mapper =
            Mappers.getMapper(WorkloadMapperI.class);

    @Test
    void toEntity_shouldMapAllFieldsCorrectly() {

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

        request.setTrainerUsername("john");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setStatus(true);
        request.setTrainingDate(LocalDate.of(2025, 5, 10));
        request.setTrainingDuration(60);
        request.setActionType(ActionType.ADD);

        Workload workload = mapper.toEntity(request);

        assertNotNull(workload);

        assertEquals("john", workload.getTrainerUsername());
        assertEquals("John", workload.getFirstName());
        assertEquals("Doe", workload.getLastName());
        assertTrue(workload.getStatus());
    }

    @Test
    void toEntity_shouldReturnNull_whenRequestIsNull() {

        Workload workload = mapper.toEntity(null);

        assertNull(workload);
    }

    @Test
    void toEntity_shouldMapFalseStatus() {

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

        request.setTrainerUsername("mike");
        request.setFirstName("Mike");
        request.setLastName("Smith");
        request.setStatus(false);

        Workload workload = mapper.toEntity(request);

        assertNotNull(workload);

        assertEquals("mike", workload.getTrainerUsername());
        assertEquals("Mike", workload.getFirstName());
        assertEquals("Smith", workload.getLastName());
        assertFalse(workload.getStatus());
    }
}