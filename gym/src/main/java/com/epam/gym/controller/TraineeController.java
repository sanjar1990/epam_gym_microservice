package com.epam.gym.controller;

import com.epam.gym.dto.*;
import com.epam.gym.service.TraineeService;
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
@RequestMapping("/api/v1/trainee")
@Tag(name = "Trainee Api list", description = "this api is for Trainee")
@RequiredArgsConstructor()
public class TraineeController {

    private final TraineeService traineeService;


    //1. Trainee Registration (POST method)
    @PostMapping("/register")
    @Operation(summary = "Create for Trainee", description = "")
    public ResponseEntity<AuthDTO> createTrainee(@Valid @RequestBody CreateTraineeRequestDTO dto) {
        return ResponseEntity.ok(traineeService.createTrainee(dto));
    }


    //    5. Get Trainee Profile (GET method)
    @PreAuthorize("hasAnyRole('ROLE_TRAINEE')")
    @GetMapping()
    @Operation(summary = "Get Trainee", description = "")
    public ResponseEntity<TraineeDTO> getTrainee() {
        return ResponseEntity.ok(traineeService.getTraineeByUsername());
    }

    //    6. Update Trainee Profile (PUT method)
    @PreAuthorize("hasAnyRole('ROLE_TRAINEE')")
    @PutMapping()
    @Operation(summary = "Update Trainee details ", description = "")
    public ResponseEntity<TraineeDTO> updateTraineeDetails(@Valid @RequestBody UpdateTraineeRequestDTO dto) {
        return ResponseEntity.ok(traineeService.updateTrainee(dto));
    }

    //    7. Delete Trainee Profile (DELETE method)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping()
    @Operation(summary = "Delete Trainee ", description = "")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTrainee(@RequestParam(name = "username") String username) {
        traineeService.deleteTrainee(username);
    }


    //    11. Update Trainee's Trainer List (PUT method)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("/{username}/trainers")
    @Operation(summary = "Update trainee's trainer list")
    public ResponseEntity<List<TrainerDTO>> updateTrainerList(@PathVariable String username,
                                                              @RequestBody UpdateTrainersRequestDTO dto) {

        return ResponseEntity.ok(traineeService.updateTrainerList(username, dto));
    }

    //    15. Activate/De-Activate Trainee (PATCH method)
    @PatchMapping("/active")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Operation(summary = " 15. Activate/De-Activate Trainee ", description = "")
    @ResponseStatus(HttpStatus.OK)
    public void changeStatusTrainee(@RequestBody ChangeStatusRequestDTO dto) {
        traineeService.changeStatusTrainee(dto);
    }
}
