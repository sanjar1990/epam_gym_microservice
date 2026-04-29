package com.epam.gym.dto;

import com.epam.gym.config.custom_anotation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChangePasswordRequestDTO {
    @NotBlank(message = "Username cannot be empty or null")
    private String username;
    @NotBlank(message = "Old password cannot be empty or null")
    private String oldPassword;
    @NotBlank(message = "New password cannot be empty or null")
    @ValidPassword
    private String newPassword;
}
