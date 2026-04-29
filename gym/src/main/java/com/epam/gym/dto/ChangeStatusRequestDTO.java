package com.epam.gym.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeStatusRequestDTO {
    @NotBlank(message = "Username cannot be empty or null")
    private String username;
    @NotNull(message = "isActive cannot be empty or null")
    private Boolean isActive;
}
