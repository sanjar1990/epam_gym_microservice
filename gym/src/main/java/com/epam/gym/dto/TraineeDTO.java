package com.epam.gym.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TraineeDTO {
    private LocalDate dateOfBirth;
    private String address;
    private UserDTO user;
    private List<TrainerDTO> trainersList;
}
