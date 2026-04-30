package com.epam.gym.dto;

import com.epam.gym.enums.ActionType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainerWorkloadRequest {
    @NotBlank(message = "Username is required")
    private String trainerUsername;
    @NotBlank(message = "firstName is required")
    private String firstName;
    @NotBlank(message = "lastName is required")
    private String lastName;
    @NotNull(message = "trainingDate is required")
    @Future(message = "trainingDate must be in the future")
    private LocalDate trainingDate;
    @NotNull(message = "trainingDuration is required")
    @Min(value = 30, message = "trainingDuration must be >= 0")
    @Max(value = 120, message = "trainingDuration must be <= 120")
    private Integer trainingDuration;
    @NotNull(message = "actionType is required")
    private ActionType actionType;
}
