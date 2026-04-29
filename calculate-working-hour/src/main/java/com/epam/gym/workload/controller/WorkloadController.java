package com.epam.gym.workload.controller;

import com.epam.gym.workload.dto.TrainerWorkloadRequest;
import com.epam.gym.workload.dto.TrainerWorkloadSummeryResponse;
import com.epam.gym.workload.service.WorkloadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{username}")
    public TrainerWorkloadSummeryResponse getMonthlyHours(
            @PathVariable String username,
            @RequestParam int year,
            @RequestParam int month) {

        return workloadService.getWorkload(username, year, month);
    }
}
