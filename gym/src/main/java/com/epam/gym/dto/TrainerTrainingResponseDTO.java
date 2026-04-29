package com.epam.gym.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainerTrainingResponseDTO {
    private Long id;
    private String trainingName;
    private LocalDate trainingDate;
    private TrainingTypeDTO trainingType;
    private Integer trainingDuration;
    private TraineeDTO trainee;
}
