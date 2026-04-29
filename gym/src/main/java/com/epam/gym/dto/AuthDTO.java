package com.epam.gym.dto;

import com.epam.gym.config.custom_anotation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthDTO {

    @NotBlank(message = "Username cannot be empty or null")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    private String username;
    @NotBlank(message = "Password cannot be empty or null")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters long")
    @ValidPassword
    private String password;
}
