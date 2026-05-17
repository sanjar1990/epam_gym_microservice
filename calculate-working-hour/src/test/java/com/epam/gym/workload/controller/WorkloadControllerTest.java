package com.epam.gym.workload.controller;


import com.epam.gym.workload.dto.TrainerWorkloadRequest;
import com.epam.gym.workload.dto.TrainerWorkloadSummeryResponse;
import com.epam.gym.workload.enums.ActionType;
import com.epam.gym.workload.service.WorkloadService;
import com.epam.gym.workload.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(WorkloadController.class)
class WorkloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkloadService workloadService;

    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void workloadAdding() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setTrainingDate(LocalDate.now().plusMonths(1));
        request.setTrainingDuration(60);
        request.setActionType(ActionType.ADD);
        request.setStatus(true);
        Mockito.doNothing().when(workloadService).updateWorkload(Mockito.any());

        mockMvc.perform(post("/api/v1/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(workloadService).updateWorkload(Mockito.any());
    }

    @Test
    void getMonthlyHours() throws Exception {
        Mockito.when(workloadService.getWorkload("john", 2024, 5))
                .thenReturn(null);

        mockMvc.perform(get("/api/v1/workload/john")
                        .param("year", "2024")
                        .param("month", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        Mockito.verify(workloadService).getWorkload("john", 2024, 5);
    }

    @Test
    void shouldReturnBadRequest_whenServiceThrows() throws Exception {
        Mockito.when(workloadService.getWorkload("john", 2024, 5))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/v1/workload/john")
                        .param("year", "2024")
                        .param("month", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("DB error"));
    }

    @Test
    void shouldHandlePostRequest_whenActionIsDelete() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setTrainingDate(LocalDate.now().plusDays(1));
        request.setTrainingDuration(60);
        request.setActionType(ActionType.DELETE);
        request.setStatus(true);
        mockMvc.perform(post("/api/v1/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(workloadService).updateWorkload(Mockito.any());
    }

    @Test
    void shouldReturnMonthlyHours() throws Exception {
        TrainerWorkloadSummeryResponse response = new TrainerWorkloadSummeryResponse();

        Mockito.when(workloadService.getWorkload("john", 2024, 5))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/workload/john")
                        .param("year", "2024")
                        .param("month", "5"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        Mockito.verify(workloadService).getWorkload("john", 2024, 5);
    }

    @Test
    void shouldReturnBadRequest_whenMonthInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/workload/john")
                        .param("year", "2024")
                        .param("month", "13"))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(workloadService);
    }

    @Test
    void shouldReturnBadRequest_whenYearInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/workload/john")
                        .param("year", "1999")
                        .param("month", "5"))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(workloadService);
    }

    @Test
    void shouldReturnBadRequest_whenParamsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/workload/john"))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(workloadService);
    }

    @Test
    void shouldReturnBadRequest_whenPostBodyInvalid() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

        mockMvc.perform(post("/api/v1/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(workloadService);
    }

    @Test
    void shouldAcceptBoundaryMonthValues() throws Exception {

        TrainerWorkloadSummeryResponse response =
                new TrainerWorkloadSummeryResponse();

        Mockito.when(workloadService.getWorkload("john", 2024, 1))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/workload/john")
                        .param("year", "2024")
                        .param("month", "1"))
                .andExpect(status().isOk());

        Mockito.when(workloadService.getWorkload("john", 2024, 12))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/workload/john")
                        .param("year", "2024")
                        .param("month", "12"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAcceptBoundaryYearValue() throws Exception {

        TrainerWorkloadSummeryResponse response =
                new TrainerWorkloadSummeryResponse();

        Mockito.when(workloadService.getWorkload("john", 2000, 5))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/workload/john")
                        .param("year", "2000")
                        .param("month", "5"))
                .andExpect(status().isOk());

        Mockito.verify(workloadService)
                .getWorkload("john", 2000, 5);
    }

    @Test
    void shouldReturnBadRequest_whenMonthIsNotNumber() throws Exception {

        mockMvc.perform(get("/api/v1/workload/john")
                        .param("year", "2024")
                        .param("month", "abc"))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(workloadService);
    }

    @Test
    void shouldReturnBadRequest_whenMonthBelowRange() throws Exception {

        mockMvc.perform(get("/api/v1/workload/john")
                        .param("year", "2024")
                        .param("month", "0"))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(workloadService);
    }

    @Test
    void shouldReturnBadRequest_whenYearIsNotNumber() throws Exception {

        mockMvc.perform(get("/api/v1/workload/john")
                        .param("year", "abcd")
                        .param("month", "5"))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(workloadService);
    }

    @Test
    void shouldReturnMethodNotAllowed_whenUsingPut() throws Exception {

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                                .put("/api/v1/workload"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void shouldReturnBadRequest_whenInvalidEnumProvided() throws Exception {

        String invalidJson = """
                {
                  "trainerUsername": "john",
                  "firstName": "John",
                  "lastName": "Doe",
                  "trainingDate": "2026-01-01",
                  "trainingDuration": 60,
                  "actionType": "INVALID",
                  "status": true
                }
                """;

        mockMvc.perform(post("/api/v1/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(workloadService);
    }

    @Test
    void shouldReturnBadRequest_whenMalformedJson() throws Exception {

        mockMvc.perform(post("/api/v1/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(workloadService);
    }

    @Test
    void shouldReturnBadRequest_whenTrainingDurationInvalidType() throws Exception {

        String invalidJson = """
                {
                  "trainerUsername": "john",
                  "firstName": "John",
                  "lastName": "Doe",
                  "trainingDate": "2026-01-01",
                  "trainingDuration": "abc",
                  "actionType": "ADD",
                  "status": true
                }
                """;

        mockMvc.perform(post("/api/v1/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(workloadService);
    }
}