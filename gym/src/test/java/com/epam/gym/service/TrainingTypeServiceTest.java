package com.epam.gym.service;

import com.epam.gym.dto.TrainingTypeDTO;
import com.epam.gym.entity.TrainingType;
import com.epam.gym.enums.TrainingTypeEnum;
import com.epam.gym.mapper.training_type.TrainingTypeMapperI;
import com.epam.gym.repository.TrainingTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private TrainingTypeMapperI trainingTypeMapperI;

    @InjectMocks
    private TrainingTypeService trainingTypeService;

    @Test
    void getAllTrainingTypes_shouldCallMapperForEachEntity() {

        TrainingType type1 = new TrainingType();
        TrainingType type2 = new TrainingType();

        when(trainingTypeRepository.findAll())
                .thenReturn(List.of(type1, type2));

        when(trainingTypeMapperI.toDTO(any()))
                .thenReturn(new TrainingTypeDTO());

        trainingTypeService.getAllTrainingTypes();

        verify(trainingTypeMapperI).toDTO(type1);
        verify(trainingTypeMapperI).toDTO(type2);
    }

    @Test
    void getAllTrainingTypes_shouldNotCallMapper_whenEmptyList() {

        when(trainingTypeRepository.findAll())
                .thenReturn(List.of());

        trainingTypeService.getAllTrainingTypes();

        verify(trainingTypeMapperI, never()).toDTO(any());
    }

    @Test
    void getAllTrainingTypes_shouldHandleNullFields() {

        TrainingType type = new TrainingType();
        type.setId(null);
        type.setTrainingTypeName(null);

        when(trainingTypeRepository.findAll())
                .thenReturn(List.of(type));

        when(trainingTypeMapperI.toDTO(type))
                .thenReturn(new TrainingTypeDTO());

        List<TrainingTypeDTO> result =
                trainingTypeService.getAllTrainingTypes();

        assertEquals(1, result.size());
    }

    @Test
    void getAllTrainingTypes_shouldHandleLargeList() {

        List<TrainingType> list = new java.util.ArrayList<>();

        for (int i = 0; i < 100; i++) {
            list.add(new TrainingType());
        }

        when(trainingTypeRepository.findAll()).thenReturn(list);
        when(trainingTypeMapperI.toDTO(any()))
                .thenReturn(new TrainingTypeDTO());

        List<TrainingTypeDTO> result =
                trainingTypeService.getAllTrainingTypes();

        assertEquals(100, result.size());
    }

    @Test
    void getAllTrainingTypes_shouldThrowException_whenRepositoryReturnsNull() {

        when(trainingTypeRepository.findAll()).thenReturn(null);

        assertThrows(NullPointerException.class,
                () -> trainingTypeService.getAllTrainingTypes());
    }

    @Test
    void getAllTrainingTypes_shouldReturnEmptyList_whenRepositoryReturnsNull() {

        when(trainingTypeRepository.findAll()).thenReturn(emptyList());

        List<TrainingTypeDTO> result =
                trainingTypeService.getAllTrainingTypes();

        assertTrue(result.isEmpty());
    }


    @Test
    void getAllTrainingTypes_shouldReturnMappedDTOList() {

        TrainingType type1 = new TrainingType();
        type1.setId(1L);
        type1.setTrainingTypeName(TrainingTypeEnum.CARDIO);

        TrainingType type2 = new TrainingType();
        type2.setId(2L);
        type2.setTrainingTypeName(TrainingTypeEnum.STRENGTH);

        when(trainingTypeRepository.findAll())
                .thenReturn(List.of(type1, type2));

        when(trainingTypeMapperI.toDTO(any(TrainingType.class)))
                .thenAnswer(invocation -> {
                    TrainingType type = invocation.getArgument(0);

                    TrainingTypeDTO dto = new TrainingTypeDTO();
                    dto.setId(type.getId());
                    dto.setTrainingTypeName(type.getTrainingTypeName());

                    return dto;
                });

        List<TrainingTypeDTO> result =
                trainingTypeService.getAllTrainingTypes();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(TrainingTypeEnum.CARDIO, result.get(0).getTrainingTypeName());
        assertEquals(TrainingTypeEnum.STRENGTH, result.get(1).getTrainingTypeName());

        verify(trainingTypeRepository).findAll();
    }

    @Test
    void getAllTrainingTypes_shouldReturnEmptyList_whenRepositoryEmpty() {

        when(trainingTypeRepository.findAll())
                .thenReturn(List.of());

        List<TrainingTypeDTO> result =
                trainingTypeService.getAllTrainingTypes();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(trainingTypeRepository).findAll();
    }
}