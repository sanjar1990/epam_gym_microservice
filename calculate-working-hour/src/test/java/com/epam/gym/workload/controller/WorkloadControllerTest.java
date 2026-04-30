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

// TODO:
//  Compilation error!
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
    void shouldHandlePostRequest() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setTrainingDate(LocalDate.now().plusMonths(1));
        request.setTrainingDuration(60);
        request.setActionType(ActionType.ADD);

        Mockito.doNothing().when(workloadService).updateWorkload(Mockito.any());

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

    // ✅ Validation test: invalid month
    @Test
    void shouldReturnBadRequest_whenMonthInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/workload/john")
                        .param("year", "2024")
                        .param("month", "13"))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(workloadService);
    }

    // ✅ Validation test: invalid year
    @Test
    void shouldReturnBadRequest_whenYearInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/workload/john")
                        .param("year", "1999")
                        .param("month", "5"))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(workloadService);
    }

    // ✅ Validation test: missing params
    @Test
    void shouldReturnBadRequest_whenParamsMissing() throws Exception {
        mockMvc.perform(get("/api/v1/workload/john"))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(workloadService);
    }

    // ✅ Validation test: invalid POST body
    @Test
    void shouldReturnBadRequest_whenPostBodyInvalid() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        // Missing required fields intentionally

        mockMvc.perform(post("/api/v1/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(workloadService);
    }
}