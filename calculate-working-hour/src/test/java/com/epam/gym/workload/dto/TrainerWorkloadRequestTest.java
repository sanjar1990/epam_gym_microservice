package com.epam.gym.workload.dto;

import com.epam.gym.workload.enums.ActionType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainerWorkloadRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation_whenRequestIsValid() {
        TrainerWorkloadRequest request = buildValidRequest();

        Set<ConstraintViolation<TrainerWorkloadRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFailValidation_whenTrainerUsernameIsBlank() {
        TrainerWorkloadRequest request = buildValidRequest();
        request.setTrainerUsername("");

        Set<ConstraintViolation<TrainerWorkloadRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "Username is required",
                violations.iterator().next().getMessage()
        );
    }

    @Test
    void shouldFailValidation_whenFirstNameIsBlank() {
        TrainerWorkloadRequest request = buildValidRequest();
        request.setFirstName("");

        Set<ConstraintViolation<TrainerWorkloadRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "firstName is required",
                violations.iterator().next().getMessage()
        );
    }

    @Test
    void shouldFailValidation_whenLastNameIsBlank() {
        TrainerWorkloadRequest request = buildValidRequest();
        request.setLastName("");

        Set<ConstraintViolation<TrainerWorkloadRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "lastName is required",
                violations.iterator().next().getMessage()
        );
    }

    @Test
    void shouldFailValidation_whenTrainingDateIsNull() {
        TrainerWorkloadRequest request = buildValidRequest();
        request.setTrainingDate(null);

        Set<ConstraintViolation<TrainerWorkloadRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "trainingDate is required",
                violations.iterator().next().getMessage()
        );
    }

    @Test
    void shouldFailValidation_whenTrainingDateIsPast() {
        TrainerWorkloadRequest request = buildValidRequest();
        request.setTrainingDate(LocalDate.now().minusDays(1));

        Set<ConstraintViolation<TrainerWorkloadRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "trainingDate must be in the future",
                violations.iterator().next().getMessage()
        );
    }

    @Test
    void shouldFailValidation_whenTrainingDurationIsBelowMinimum() {
        TrainerWorkloadRequest request = buildValidRequest();
        request.setTrainingDuration(20);

        Set<ConstraintViolation<TrainerWorkloadRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "trainingDuration must be >= 30",
                violations.iterator().next().getMessage()
        );
    }

    @Test
    void shouldFailValidation_whenTrainingDurationIsAboveMaximum() {
        TrainerWorkloadRequest request = buildValidRequest();
        request.setTrainingDuration(150);

        Set<ConstraintViolation<TrainerWorkloadRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "trainingDuration must be <= 120",
                violations.iterator().next().getMessage()
        );
    }

    @Test
    void shouldFailValidation_whenActionTypeIsNull() {
        TrainerWorkloadRequest request = buildValidRequest();
        request.setActionType(null);

        Set<ConstraintViolation<TrainerWorkloadRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "actionType is required",
                violations.iterator().next().getMessage()
        );
    }

    @Test
    void shouldFailValidation_whenStatusIsNull() {
        TrainerWorkloadRequest request = buildValidRequest();
        request.setStatus(null);

        Set<ConstraintViolation<TrainerWorkloadRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertEquals(
                "status is required",
                violations.iterator().next().getMessage()
        );
    }

    private TrainerWorkloadRequest buildValidRequest() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("trainer1");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setTrainingDate(LocalDate.now().plusDays(1));
        request.setTrainingDuration(60);
        request.setActionType(ActionType.ADD);
        request.setStatus(true);

        return request;
    }
}