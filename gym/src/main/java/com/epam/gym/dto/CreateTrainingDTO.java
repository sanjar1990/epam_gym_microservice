package com.epam.gym.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateTrainingDTO {
    @NotBlank(message = "Trainee username cannot be empty or null")
    private String traineeUsername;
    //    @NotBlank(message = "Trainer username cannot be empty or null")
//    private String trainerUsername;
    @NotNull(message = "Training date cannot be empty or null")
    private LocalDate trainingDate;
    @NotNull(message = "Training duration cannot be empty or null")
    private Integer trainingDuration;
    @NotNull(message = "Training type id cannot be empty or null")
    private Long trainingTypeId;
}
