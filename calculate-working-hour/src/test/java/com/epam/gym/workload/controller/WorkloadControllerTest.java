package com.epam.gym.workload.controller;


import com.epam.gym.workload.dto.TrainerWorkloadRequest;
import com.epam.gym.workload.dto.TrainerWorkloadSummeryResponse;
import com.epam.gym.workload.enums.ActionType;
import com.epam.gym.workload.service.WorkloadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TODO:
//  Compilation error!
@WebMvcTest(WorkloadController.class)
class WorkloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkloadService workloadService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldHandlePostRequest() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setUsername("john");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setIsActive(true);
        request.setTrainingDate(LocalDate.now());
        request.setTrainingDuration(60);
        request.setActionType(ActionType.ADD); // or whatever enum you have

        Mockito.doNothing().when(workloadService).calculateWorkingHours(Mockito.any());

        mockMvc.perform(post("/api/v1/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Mockito.verify(workloadService).calculateWorkingHours(Mockito.any());
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
}