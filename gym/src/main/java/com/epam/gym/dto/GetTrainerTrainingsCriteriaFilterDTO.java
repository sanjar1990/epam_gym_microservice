package com.epam.gym.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class GetTrainerTrainingsCriteriaFilterDTO {
    private String trainerUsername;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String traineeName;
    private String trainingType;
}
