package com.epam.gym.workload.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class YearSummary {

    private Integer year;

    private List<MonthSummary> months;
}