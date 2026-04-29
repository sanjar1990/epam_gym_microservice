package com.epam.gym.workload.controller;

import com.epam.gym.workload.dto.TrainerWorkloadRequest;
import com.epam.gym.workload.dto.TrainerWorkloadSummeryResponse;
import com.epam.gym.workload.dto.WorkloadCalculateRequestDTO;
import com.epam.gym.workload.service.WorkloadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/workload")
@RequiredArgsConstructor
public class WorkloadController {
    private final WorkloadService workloadService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void workloadAdding(@Valid @RequestBody TrainerWorkloadRequest request) {
        System.out.println("CONTROLLER");
        workloadService.calculateWorkingHours(request);
    }

    @GetMapping
    public TrainerWorkloadSummeryResponse getMonthlyHours(
            @RequestBody WorkloadCalculateRequestDTO dto) {

        return workloadService.getWorkload(dto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteWorkload(@Valid @RequestBody List<Long> trainingIdList) {

        workloadService.deleteWorkload(trainingIdList);
    }
}
