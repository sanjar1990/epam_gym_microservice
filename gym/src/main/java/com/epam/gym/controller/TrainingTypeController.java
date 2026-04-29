package com.epam.gym.controller;

import com.epam.gym.dto.TrainingTypeDTO;
import com.epam.gym.service.TrainingTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/training-type")
@Tag(name = "Training Type Api", description = "this api is for Training Type")
@RequiredArgsConstructor()
public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;

    //17. Get Training types (GET method)
    @GetMapping()
    @Operation(summary = "Get Training types", description = "")
    public ResponseEntity<List<TrainingTypeDTO>> getTrainingTypes() {
        return ResponseEntity.ok(trainingTypeService.getAllTrainingTypes());
    }

}
