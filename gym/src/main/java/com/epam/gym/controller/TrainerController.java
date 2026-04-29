package com.epam.gym.controller;

import com.epam.gym.dto.*;
import com.epam.gym.service.TrainerService;
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
@RequestMapping("/api/v1/trainer")
@Tag(name = "Trainer Api list", description = "this api is for Trainer")
@RequiredArgsConstructor()
public class TrainerController {

    private final TrainerService trainerService;

    //1. Trainer create (POST method)
    @PostMapping("/register")
    @Operation(summary = "Create for Trainer", description = "")
    public ResponseEntity<AuthDTO> createTrainer(
            @Valid @RequestBody CreateTrainerRequestDTO dto) {
        return ResponseEntity.ok(trainerService.createTrainer(dto));
    }

    //    8. Get Trainer Profile (GET method)
    @PreAuthorize("hasAnyRole('ROLE_TRAINER')")
    @GetMapping()
    @Operation(summary = "Get Trainer", description = "")
    public ResponseEntity<TrainerDTO> getTrainer() {
        return ResponseEntity.ok(trainerService.getTrainerByUsername());
    }

    //    9. Update Trainer Profile (PUT method)
    @PreAuthorize("hasAnyRole('ROLE_TRAINER')")
    @PutMapping()
    @Operation(summary = "Update Trainer details ", description = "")
    public ResponseEntity<TrainerDTO> updateTrainerDetails(@Valid @RequestBody UpdateTrainerRequestDTO dto) {
        return ResponseEntity.ok(trainerService.updateTrainer(dto));
    }

    //    10. Get not assigned on trainee active trainers. (GET method)
    @PreAuthorize("hasAnyRole('ROLE_TRAINER','ROLE_ADMIN')")
    @GetMapping("/unassigned/{username}")
    @Operation(summary = "Get not assigned on trainee active trainers.  ", description = "")
    public ResponseEntity<List<TrainerDTO>> getTrainerNotAssignedOnTrainee(
            @PathVariable(name = "username") String traineeUsername) {
        return ResponseEntity.ok(trainerService.getTrainersNotAssignedOnTrainee(traineeUsername));
    }

    //    16. Activate/De-Activate Trainer (PATCH method)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PatchMapping("/active")
    @Operation(summary = " 15. Activate/De-Activate Trainer ", description = "")
    @ResponseStatus(HttpStatus.OK)
    public void changeStatusTrainer(
            @RequestBody ChangeStatusRequestDTO dto) {
        trainerService.changeStatusTrainee(dto);
    }


}
