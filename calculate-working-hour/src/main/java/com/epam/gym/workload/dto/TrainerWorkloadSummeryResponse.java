package com.epam.gym.workload.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TrainerWorkloadSummeryResponse {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private List<TrainingDate> years;
}
