package com.epam.gym.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TrainerDTO {

    private UserDTO user;
    private TrainingTypeDTO trainingType;
    private List<TraineeDTO> traineesList;
}
