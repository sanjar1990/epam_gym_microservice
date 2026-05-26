package com.epam.gym.workload.service;

import com.epam.gym.workload.dto.MonthlySummaryDTO;
import com.epam.gym.workload.dto.TrainerWorkloadRequest;
import com.epam.gym.workload.dto.TrainerWorkloadSummeryResponse;
import com.epam.gym.workload.dto.TrainingDate;
import com.epam.gym.workload.entity.MonthSummary;
import com.epam.gym.workload.entity.Workload;
import com.epam.gym.workload.entity.YearSummary;
import com.epam.gym.workload.enums.ActionType;
import com.epam.gym.workload.mapper.WorkloadMapperI;
import com.epam.gym.workload.repository.WorkloadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadService {
    private final WorkloadRepository workloadRepository;
    private final WorkloadMapperI workloadMapperI;

    public void updateWorkload(TrainerWorkloadRequest request) {
        if (request == null) {
            throw new NullPointerException("Request cannot be null");
        }
        log.info("[{}] Processing training workload event for trainer={}",
                MDC.get("transactionId"),
                request.getTrainerUsername());
        Workload workload;
        if (request.getActionType() == ActionType.ADD) {
            workload =
                    workloadRepository.findByTrainerUsername(request.getTrainerUsername())
                            .orElseGet(() -> workloadMapperI.toEntity(request));

            int yearValue = request.getTrainingDate().getYear();
            int monthValue = request.getTrainingDate().getMonthValue();
            if (workload.getYears() == null) {
                workload.setYears(new ArrayList<>());
            }
            YearSummary yearSummary =
                    workload.getYears()
                            .stream()
                            .filter(y -> y.getYear().equals(yearValue))
                            .findFirst()
                            .orElseGet(() -> {
                                YearSummary newYear =
                                        YearSummary.builder()
                                                .year(yearValue)
                                                .build();

                                workload.getYears().add(newYear);
                                return newYear;
                            });
            if (yearSummary.getMonths() == null) {
                yearSummary.setMonths(new ArrayList<>());
            }

            MonthSummary monthSummary = yearSummary.getMonths()
                    .stream()
                    .filter(m -> m.getMonth().equals(monthValue))
                    .findFirst()
                    .orElseGet(() -> {
                        MonthSummary newMonth =
                                MonthSummary.builder()
                                        .month(monthValue)
                                        .trainingSummaryDuration(0)
                                        .build();

                        yearSummary.getMonths().add(newMonth);
                        return newMonth;
                    });
            monthSummary.setTrainingSummaryDuration(
                    monthSummary.getTrainingSummaryDuration()
                            + request.getTrainingDuration());
            workload.setYears(workload.getYears());
            workloadRepository.save(workload);
            log.info("[{}] Successfully saved trainer summary for={}",
                    MDC.get("transactionId"),
                    request.getTrainerUsername());
        } else {
            workload = workloadRepository
                    .findByTrainerUsername(request.getTrainerUsername())
                    .orElseThrow(() -> new RuntimeException("Workload not found"));

            int yearValue = request.getTrainingDate().getYear();
            int monthValue = request.getTrainingDate().getMonthValue();

            YearSummary yearSummary = workload.getYears()
                    .stream()
                    .filter(y -> y.getYear().equals(yearValue))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Year not found"));


            MonthSummary monthSummary = yearSummary.getMonths()
                    .stream()
                    .filter(m -> m.getMonth().equals(monthValue))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Month not found"));

            int updatedDuration =
                    monthSummary.getTrainingSummaryDuration()
                            - request.getTrainingDuration();

            if (updatedDuration <= 0) {
                yearSummary.getMonths().remove(monthSummary);
            } else {
                monthSummary.setTrainingSummaryDuration(updatedDuration);
            }

            if (yearSummary.getMonths().isEmpty()) {
                workload.getYears().remove(yearSummary);
            }

            workloadRepository.save(workload);

            log.info("[{}] Successfully removed trainer summary for={}",
                    MDC.get("transactionId"),
                    request.getTrainerUsername());
        }
    }


    public TrainerWorkloadSummeryResponse getWorkload(
            String trainerUsername,
            int year,
            int month
    ) {

        List<MonthlySummaryDTO> rows =
                workloadRepository.getMonthlySummary(trainerUsername);

        if (rows.isEmpty()) {
            return null;
        }

        TrainerWorkloadSummeryResponse response =
                new TrainerWorkloadSummeryResponse();

        List<TrainingDate> list = new ArrayList<>();

        for (MonthlySummaryDTO row : rows) {

            int rowYear = row.getYear();
            int rowMonth = row.getMonth();
            int duration = row.getTotalDuration();

            if (rowYear == year && rowMonth == month) {

                response.setUsername(row.getTrainerUsername());
                response.setFirstName(row.getFirstName());
                response.setLastName(row.getLastName());

                TrainingDate td = new TrainingDate();
                td.setYear(rowYear);
                td.setMonth(rowMonth);
                td.setDuration(duration);

                list.add(td);
            }
        }

        response.setYears(list);

        return response;
    }


}
