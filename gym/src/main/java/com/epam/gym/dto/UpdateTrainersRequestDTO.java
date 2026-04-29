package com.epam.gym.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateTrainersRequestDTO {
    @NotNull(message = "Trainer usernames cannot be empty or null")
    private List<String> trainerUsernames;
}
