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
        // TODO:
        //  Use logging instead of System.out.println, consider meaningful messages.
        //  Please check other places in the project using System.out.println and change them to logging as well
        System.out.println("CONTROLLER");
        workloadService.calculateWorkingHours(request);
    }

    // TODO:
    //  1. GET requests are not supposed to have a body. Please don't change to POST, work with params instead
    //  trainerUsername, year, month - should be enough.
    //  2. Let's return a flat structure without extra information { username, year, month, workload }
    @GetMapping
    public TrainerWorkloadSummeryResponse getMonthlyHours(
            @RequestBody WorkloadCalculateRequestDTO dto) {

        return workloadService.getWorkload(dto);
    }

    // TODO:
    //  use existing POST "/api/v1/workload" with actionType=DELETE
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteWorkload(@Valid @RequestBody List<Long> trainingIdList) {

        workloadService.deleteWorkload(trainingIdList);
    }
}
