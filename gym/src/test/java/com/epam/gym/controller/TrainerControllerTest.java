package com.epam.gym.controller;

import com.epam.gym.dto.*;
import com.epam.gym.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainerService trainerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTrainer_shouldReturn200() throws Exception {

        CreateTrainerRequestDTO dto = new CreateTrainerRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Smith");
        dto.setTrainingTypeId(1L);

        AuthDTO response = new AuthDTO("john.smith", "pass123");

        when(trainerService.createTrainer(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/trainer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.smith"));

        verify(trainerService).createTrainer(any());
    }

    @Test
    void getTrainer_shouldReturnDTO() throws Exception {

        TrainerDTO dto = new TrainerDTO();
        UserDTO user = new UserDTO();
        user.setUsername("john");
        dto.setUser(user);

        when(trainerService.getTrainerByUsername())
                .thenReturn(dto);

        mockMvc.perform(get("/api/v1/trainer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("john"));

        verify(trainerService).getTrainerByUsername();
    }


    @Test
    void updateTrainer_shouldReturnUpdatedDTO() throws Exception {

        UpdateTrainerRequestDTO request = new UpdateTrainerRequestDTO();
        request.setFirstName("New");
        request.setLastName("Name");
        request.setTrainingTypeId(5L);
        request.setIsActive(true);

        TrainerDTO response = new TrainerDTO();
        UserDTO user = new UserDTO();
        user.setUsername("john");
        response.setUser(user);

        when(trainerService.updateTrainer(any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/trainer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("john"));

        verify(trainerService).updateTrainer(any());
    }


    @Test
    void getTrainerNotAssignedOnTrainee_shouldReturnList() throws Exception {

        TrainerDTO trainerDTO = new TrainerDTO();
        UserDTO user = new UserDTO();
        user.setUsername("trainer1");
        trainerDTO.setUser(user);

        when(trainerService.getTrainersNotAssignedOnTrainee("john"))
                .thenReturn(List.of(trainerDTO));

        mockMvc.perform(get("/api/v1/trainer/unassigned/john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.username").value("trainer1"));

        verify(trainerService).getTrainersNotAssignedOnTrainee("john");
    }

    @Test
    void changeStatusTrainer_shouldReturn200() throws Exception {

        ChangeStatusRequestDTO dto = new ChangeStatusRequestDTO();
        dto.setUsername("john");
        dto.setIsActive(false);

        doNothing().when(trainerService).changeStatusTrainee(any());

        mockMvc.perform(patch("/api/v1/trainer/active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(trainerService).changeStatusTrainee(any());
    }


    @Test
    void createTrainer_shouldReturn400_whenInvalid() throws Exception {

        CreateTrainerRequestDTO dto = new CreateTrainerRequestDTO();

        mockMvc.perform(post("/api/v1/trainer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}