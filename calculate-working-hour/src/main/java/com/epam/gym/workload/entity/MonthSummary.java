package com.epam.gym.workload.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class MonthSummary {

    private Integer month;

    private Integer trainingSummaryDuration;
}