package com.epam.gym.controller;


import com.epam.gym.dto.TrainingTypeDTO;
import com.epam.gym.enums.TrainingTypeEnum;
import com.epam.gym.service.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrainingTypeController.class)
class TrainingTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainingTypeService trainingTypeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getTrainingTypes_shouldReturnList() throws Exception {

        TrainingTypeDTO type1 = new TrainingTypeDTO();
        type1.setId(1L);
        type1.setTrainingTypeName(TrainingTypeEnum.CARDIO);

        TrainingTypeDTO type2 = new TrainingTypeDTO();
        type2.setId(2L);
        type2.setTrainingTypeName(TrainingTypeEnum.STRENGTH);

        when(trainingTypeService.getAllTrainingTypes())
                .thenReturn(List.of(type1, type2));

        mockMvc.perform(get("/api/v1/training-type"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].trainingTypeName").value("CARDIO"))
                .andExpect(jsonPath("$[1].trainingTypeName").value("STRENGTH"));

        verify(trainingTypeService).getAllTrainingTypes();
    }

    @Test
    void getTrainingTypes_shouldReturnEmptyList() throws Exception {

        when(trainingTypeService.getAllTrainingTypes())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/training-type"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(content().json("[]"));

        verify(trainingTypeService).getAllTrainingTypes();
    }
}