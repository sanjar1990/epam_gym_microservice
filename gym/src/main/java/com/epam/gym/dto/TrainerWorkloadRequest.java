package com.epam.gym.dto;

import com.epam.gym.enums.ActionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainerWorkloadRequest {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "firstName is required")
    private String firstName;
    @NotBlank(message = "lastName is required")
    private String lastName;
    @NotNull(message = "isActive is required")
    private Boolean isActive;
    @NotNull(message = "trainingDate is required")
    private LocalDate trainingDate;
    @NotNull(message = "trainingDuration is required")
    private Integer trainingDuration;
    @NotNull(message = "actionType is required")
    private ActionType actionType;
    @NotNull(message = "trainingId is required")
    private Long trainingId;
}
