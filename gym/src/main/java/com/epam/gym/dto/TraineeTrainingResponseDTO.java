package com.epam.gym.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TraineeTrainingResponseDTO {
    private Long id;
    private String trainingName;
    private LocalDate trainingDate;
    private TrainingTypeDTO trainingType;
    private Integer trainingDuration;
    private TrainerDTO trainer;
}
