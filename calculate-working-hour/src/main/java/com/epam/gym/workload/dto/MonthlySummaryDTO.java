package com.epam.gym.workload.dto;

public interface MonthlySummaryDTO {

    String getTrainerUsername();

    String getFirstName();

    String getLastName();

    Integer getYear();

    Integer getMonth();

    Integer getTotalDuration();
}
