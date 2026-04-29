package com.epam.gym.controller;

import com.epam.gym.dto.*;
import com.epam.gym.service.TraineeService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TraineeController.class)
@AutoConfigureMockMvc(addFilters = false)
class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TraineeService traineeService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void createTrainee_shouldReturn200() throws Exception {

        CreateTraineeRequestDTO dto = new CreateTraineeRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setAddress("Seoul");
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));

        AuthDTO response = new AuthDTO("john.doe", "pass123");

        when(traineeService.createTrainee(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/trainee/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"));

        verify(traineeService).createTrainee(any());
    }


    @Test
    void getTrainee_shouldReturnDTO() throws Exception {

        TraineeDTO dto = new TraineeDTO();
        UserDTO user = new UserDTO();
        user.setUsername("john");
        dto.setUser(user);

        when(traineeService.getTraineeByUsername()).thenReturn(dto);

        mockMvc.perform(get("/api/v1/trainee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("john"));

        verify(traineeService).getTraineeByUsername();
    }


    @Test
    void updateTrainee_shouldReturnUpdatedDTO() throws Exception {

        UpdateTraineeRequestDTO request = new UpdateTraineeRequestDTO();
        request.setFirstName("New");
        request.setLastName("Name");
        request.setAddress("Busan");
        request.setDateOfBirth(LocalDate.of(1995, 5, 5));
        request.setIsActive(true);

        TraineeDTO response = new TraineeDTO();
        UserDTO user = new UserDTO();
        user.setUsername("john");
        response.setUser(user);

        when(traineeService.updateTrainee(any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/trainee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("john"));

        verify(traineeService).updateTrainee(any());
    }


    @Test
    void deleteTrainee_shouldReturn200() throws Exception {

        doNothing().when(traineeService).deleteTrainee("john");

        mockMvc.perform(delete("/api/v1/trainee")
                        .param("username", "john"))
                .andExpect(status().isOk());

        verify(traineeService).deleteTrainee("john");
    }


    @Test
    void updateTrainerList_shouldReturnList() throws Exception {

        UpdateTrainersRequestDTO request = new UpdateTrainersRequestDTO();
        request.setTrainerUsernames(List.of("trainer1"));

        TrainerDTO trainerDTO = new TrainerDTO();
        UserDTO user = new UserDTO();
        user.setUsername("trainer1");
        trainerDTO.setUser(user);

        when(traineeService.updateTrainerList(eq("john"), any()))
                .thenReturn(List.of(trainerDTO));

        mockMvc.perform(put("/api/v1/trainee/john/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.username").value("trainer1"));

        verify(traineeService).updateTrainerList(eq("john"), any());
    }


    @Test
    void changeStatusTrainee_shouldReturn200() throws Exception {

        ChangeStatusRequestDTO dto = new ChangeStatusRequestDTO();
        dto.setUsername("john");
        dto.setIsActive(false);

        doNothing().when(traineeService).changeStatusTrainee(any());

        mockMvc.perform(patch("/api/v1/trainee/active")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(traineeService).changeStatusTrainee(any());
    }

    @Test
    void createTrainee_shouldReturn400_whenInvalid() throws Exception {

        CreateTraineeRequestDTO dto = new CreateTraineeRequestDTO();

        mockMvc.perform(post("/api/v1/trainee/register"))
                .andExpect(status().isBadRequest());
    }
}