package com.epam.gym.workload.dto;

import com.epam.gym.workload.enums.ActionType;
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
    @FutureOrPresent(message = "trainingDate must be in the future")
    private LocalDate trainingDate;
    @NotNull(message = "trainingDuration is required")
    @Min(value = 30, message = "trainingDuration must be >= 30")
    @Max(value = 120, message = "trainingDuration must be <= 120")
    private Integer trainingDuration;
    @NotNull(message = "actionType is required")
    private ActionType actionType;
    // TODO:
    //  There trainingId is not expected in request body in the task because workload service
    //  does not need to know about trainingId neither to ADD nor to DELETE workload. Work with dates please
    //done
}
