package com.epam.gym.controller;

import com.epam.gym.dto.*;
import com.epam.gym.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainingService trainingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getTraineeTrainings_shouldReturnList() throws Exception {

        GetTraineeTrainingsCriteriaFilterDTO request =
                new GetTraineeTrainingsCriteriaFilterDTO();

        TraineeTrainingResponseDTO responseDTO =
                new TraineeTrainingResponseDTO();
        responseDTO.setTrainingDate(LocalDate.now());

        when(trainingService.getTrainingsByTraineeUsernameCriteria(any()))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(post("/api/v1/training/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingDate").exists());

        verify(trainingService)
                .getTrainingsByTraineeUsernameCriteria(any());
    }

    @Test
    void getTrainerTrainings_shouldReturnList() throws Exception {

        GetTrainerTrainingsCriteriaFilterDTO request =
                new GetTrainerTrainingsCriteriaFilterDTO();

        TrainerTrainingResponseDTO responseDTO =
                new TrainerTrainingResponseDTO();
        responseDTO.setTrainingDate(LocalDate.now());

        when(trainingService.getTrainingsByTrainerUsernameCriteria(any()))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(post("/api/v1/training/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingDate").exists());

        verify(trainingService)
                .getTrainingsByTrainerUsernameCriteria(any());
    }

    @Test
    void addTraining_shouldReturn200() throws Exception {

        CreateTrainingDTO request = new CreateTrainingDTO();
        request.setTraineeUsername("john");
        request.setTrainingTypeId(1L);
        request.setTrainingDate(LocalDate.now().plusMonths(2));
        request.setTrainingDuration(60);

        when(trainingService.addTraining(any(), any()))
                .thenReturn(1L);

        mockMvc.perform(post("/api/v1/training")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1L));

        verify(trainingService).addTraining(any(), any());
    }

    @Test
    void addTraining_shouldReturn400_whenInvalid() throws Exception {

        CreateTrainingDTO request = new CreateTrainingDTO(); // invalid

        mockMvc.perform(post("/api/v1/training")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}