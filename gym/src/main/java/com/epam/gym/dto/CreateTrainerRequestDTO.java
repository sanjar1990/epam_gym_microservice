package com.epam.gym.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTrainerRequestDTO {
    @NotBlank(message = "First name cannot be empty or null")
    private String firstName;
    @NotBlank(message = "Last name cannot be empty or null")
    private String lastName;
    @NotNull(message = "specialization cannot be empty or null")
    private Long trainingTypeId;
}
