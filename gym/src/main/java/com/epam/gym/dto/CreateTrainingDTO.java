package com.epam.gym.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateTrainingDTO {
    @NotBlank(message = "Trainee username cannot be empty or null")
    private String traineeUsername;
    @NotNull(message = "Training date cannot be empty or null")
    @Future(message = "Date of birth must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;
    @NotNull(message = "Training duration cannot be empty or null")
    @Min(value = 10, message = "Training duration must be at least 10 minutes")
    @Max(value = 120, message = "Training duration must not exceed 120 minutes")
    private Integer trainingDuration;
    @NotNull(message = "Training type id cannot be empty or null")
    private Long trainingTypeId;
}
