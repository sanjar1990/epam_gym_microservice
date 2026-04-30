package com.epam.gym.workload.service;

import com.epam.gym.workload.dto.TrainerWorkloadRequest;
import com.epam.gym.workload.dto.TrainerWorkloadSummeryResponse;
import com.epam.gym.workload.dto.TrainingDate;
import com.epam.gym.workload.entity.Workload;
import com.epam.gym.workload.enums.ActionType;
import com.epam.gym.workload.mapper.WorkloadMapperI;
import com.epam.gym.workload.repository.WorkloadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadService {
    private final WorkloadRepository workloadRepository;
    private final WorkloadMapperI workloadMapperI;

    // TODO:
    //  1. Have you added validation rules for year, month and duration? yes i added
    //  2. 'calculateWorkingHours' suppose to return something, right? Seems like method should be named 'updateWorkload' or similar
    public void updateWorkload(TrainerWorkloadRequest request) {
        Workload workload;
        if (request.getActionType() == ActionType.ADD) {
            workload = workloadMapperI.toEntity(request);
            workloadRepository.save(workload);
        } else {
            workloadRepository.deleteWorkloads(request.getTrainingDate(), request.getTrainingDuration());
        }
    }


    public TrainerWorkloadSummeryResponse getWorkload(String trainerUsername, int year, int month) {

        List<Object[]> rows = workloadRepository.getMonthlySummary(trainerUsername);

        if (rows.isEmpty()) {
            return null;
        }

        TrainerWorkloadSummeryResponse response = new TrainerWorkloadSummeryResponse();
        List<TrainingDate> list = new ArrayList<>();

        for (Object[] row : rows) {

            int rowYear = (int) row[4];
            int rowMonth = (int) row[5];
            int duration = ((Long) row[6]).intValue();

            if (rowYear == year && rowMonth == month) {
                response.setUsername((String) row[0]);
                response.setFirstName((String) row[1]);
                response.setLastName((String) row[2]);
                response.setIsActive((Boolean) row[3]);

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
