package com.epam.gym.controller;

import com.epam.gym.dto.*;
import com.epam.gym.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/training")
@Tag(name = "Training Api list", description = "this api is for Training")
@RequiredArgsConstructor()
public class TrainingController {
    private final TrainingService trainingService;

    //    12. Get Trainee Trainings List (GET method)
    @PreAuthorize("hasAnyRole('ROLE_TRAINEE','ROLE_ADMIN')")
    @PostMapping("/trainee")
    @Operation(summary = "Get Trainee Trainings List", description = "")
    public ResponseEntity<List<TraineeTrainingResponseDTO>> getTraineeTrainings(
            @RequestBody GetTraineeTrainingsCriteriaFilterDTO dto) {
        return ResponseEntity.ok(trainingService.getTrainingsByTraineeUsernameCriteria(dto));
    }

    //    13. Get Trainer Trainings List (GET method)
    @PreAuthorize("hasAnyRole('ROLE_TRAINER','ROLE_ADMIN')")
    @PostMapping("/trainer")
    @Operation(summary = "Get Trainer Trainings List", description = "")
    public ResponseEntity<List<TrainerTrainingResponseDTO>> getTrainerTrainings(
            @RequestBody GetTrainerTrainingsCriteriaFilterDTO dto) {
        return ResponseEntity.ok(trainingService.getTrainingsByTrainerUsernameCriteria(dto));
    }


    //    14 Add Training (POST method)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping()
    @Operation(summary = " 14 Add Training", description = "")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Long> addTraining(@Valid @RequestBody CreateTrainingDTO dto,
                                            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(trainingService.addTraining(dto, token));
    }

    //    14 Add Training (POST method)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping("/{trainingId}")
    @Operation(summary = " 14 Adddelete Training", description = "")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTraining(@PathVariable Long trainingId, @RequestHeader("Authorization") String token) {
        trainingService.deleteTraining(trainingId, token);
    }

}
