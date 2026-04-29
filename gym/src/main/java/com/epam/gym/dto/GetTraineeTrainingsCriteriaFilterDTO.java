package com.epam.gym.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class GetTraineeTrainingsCriteriaFilterDTO {
    @NotBlank(message = "Trainee username cannot be empty or null")
    private String traineeUsername;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String trainerName;
    private String trainingType;
}
