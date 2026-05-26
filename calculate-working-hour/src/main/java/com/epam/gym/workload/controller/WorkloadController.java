package com.epam.gym.workload.controller;

import com.epam.gym.workload.dto.TrainerWorkloadRequest;
import com.epam.gym.workload.dto.TrainerWorkloadSummeryResponse;
import com.epam.gym.workload.service.WorkloadService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/v1/workload")
@RequiredArgsConstructor
public class WorkloadController {
    private final WorkloadService workloadService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void workloadAdding(@Valid @RequestBody TrainerWorkloadRequest request) {
        workloadService.updateWorkload(request);
    }

    @GetMapping("/{username}")
    public TrainerWorkloadSummeryResponse getMonthlyHours(
            @PathVariable("username") String trainerUsername,
            @RequestParam
            @Min(value = 2000, message = "Year must be >= 2000")
            int year,

            @RequestParam
            @Min(value = 1, message = "Month must be between 1 and 12")
            @Max(value = 12, message = "Month must be between 1 and 12") int month) {
        return workloadService.getWorkload(trainerUsername, year, month);
    }

}
