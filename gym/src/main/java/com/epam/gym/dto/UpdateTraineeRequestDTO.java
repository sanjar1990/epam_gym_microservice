package com.epam.gym.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateTraineeRequestDTO {

    @NotBlank(message = "First name cannot be empty or null")
    private String firstName;
    @NotBlank(message = "Last name cannot be empty or null")
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    @NotNull(message = "isActive cannot be empty or null")
    private Boolean isActive;
}
