package com.epam.gym.workload.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class WorkloadCalculateRequestDTO {
    @NotBlank(message = "Trainer username cannot be empty or null")
    private String trainerUsername;
    @NotNull(message = "Date cannot be empty or null")
    private LocalDate date;
}
