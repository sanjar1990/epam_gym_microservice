package com.epam.gym.service;

import com.epam.gym.dto.*;
import com.epam.gym.entity.*;
import com.epam.gym.mapper.training.TrainingMapperI;
import com.epam.gym.repository.TrainingRepository;
import com.epam.gym.util.SpringSecurityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingMapperI trainingMapperI;

    @Mock
    private WorkloadConnectionI workloadClientService;

    @InjectMocks
    private TrainingService trainingService;

    private static final String TOKEN = "Bearer test-token";


    @Test
    void addTraining_shouldSaveAndReturnId() {

        CreateTrainingDTO dto = new CreateTrainingDTO();
        dto.setTraineeUsername("john");
        dto.setTrainingTypeId(1L);
        dto.setTrainingDate(LocalDate.now());
        dto.setTrainingDuration(60);

        Trainee trainee = new Trainee();
        trainee.setTrainings(new ArrayList<>());
        trainee.setTrainers(new HashSet<>());

        TrainingType type = new TrainingType();
        type.setId(1L);

        User user = new User();
        user.setUsername("mike");

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setTrainingType(type);
        trainer.setTrainees(new HashSet<>());

        Training training = new Training();
        training.setId(100L);
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);

        try (MockedStatic<SpringSecurityUtil> mocked = mockStatic(SpringSecurityUtil.class)) {

            mocked.when(SpringSecurityUtil::getCurrentUser).thenReturn(user);

            when(traineeService.getTrainee("john")).thenReturn(trainee);
            when(trainerService.getTrainerEntityByUsername("mike")).thenReturn(trainer);
            when(trainingMapperI.toEntity(any())).thenReturn(training);
            when(trainingRepository.save(training)).thenReturn(training);

            Long result = trainingService.addTraining(dto, TOKEN);

            assertEquals(100L, result);

            verify(trainingRepository).save(training);
            verify(workloadClientService)
                    .updateWorkload(any(), eq(TOKEN), any());
        }
    }


    @Test
    void addTraining_shouldThrow_whenTrainingTypeMismatch() {

        CreateTrainingDTO dto = new CreateTrainingDTO();
        dto.setTraineeUsername("john");
        dto.setTrainingTypeId(99L);

        Trainee trainee = new Trainee();
        trainee.setTrainings(new ArrayList<>());
        trainee.setTrainers(new HashSet<>());

        TrainingType type = new TrainingType();
        type.setId(1L);

        User user = new User();
        user.setUsername("mike");

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setTrainingType(type);

        try (MockedStatic<SpringSecurityUtil> mocked = mockStatic(SpringSecurityUtil.class)) {

            mocked.when(SpringSecurityUtil::getCurrentUser).thenReturn(user);

            when(traineeService.getTrainee("john")).thenReturn(trainee);
            when(trainerService.getTrainerEntityByUsername("mike")).thenReturn(trainer);

            assertThrows(RuntimeException.class,
                    () -> trainingService.addTraining(dto, TOKEN));

            verify(trainingRepository, never()).save(any());
            verifyNoInteractions(trainingMapperI);
        }
    }

    @Test
    void addTraining_shouldThrow_whenMapperReturnsNull() {

        CreateTrainingDTO dto = new CreateTrainingDTO();
        dto.setTraineeUsername("john");
        dto.setTrainingTypeId(1L);

        Trainee trainee = new Trainee();
        trainee.setTrainings(new ArrayList<>());
        trainee.setTrainers(new HashSet<>());


        TrainingType type = new TrainingType();
        type.setId(1L);

        User user = new User();
        user.setUsername("mike");

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setTrainingType(type);
        trainer.setTrainees(new HashSet<>());

        try (MockedStatic<SpringSecurityUtil> mocked =
                     mockStatic(SpringSecurityUtil.class)) {

            mocked.when(SpringSecurityUtil::getCurrentUser).thenReturn(user);

            when(trainerService.getTrainerEntityByUsername("mike"))
                    .thenReturn(trainer);

            when(traineeService.getTrainee("john"))
                    .thenReturn(trainee);

            when(trainingMapperI.toEntity(any())).thenReturn(null);

            assertThrows(NullPointerException.class,
                    () -> trainingService.addTraining(dto, "token"));


        }
    }

    @Test
    void deleteTraining_shouldDeleteAndSendWorkload() {

        Training training = new Training();
        training.setId(1L);

        User user = new User();
        user.setUsername("mike");

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        training.setTrainer(trainer);

        when(trainingRepository.findById(1L))
                .thenReturn(Optional.of(training));

        trainingService.deleteTraining(1L, TOKEN);

        verify(trainingRepository).deleteById(1L);
        verify(workloadClientService)
                .updateWorkload(any(), eq(TOKEN), any());
    }

    @Test
    void deleteTraining_shouldThrow_whenNotFound() {

        when(trainingRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> trainingService.deleteTraining(1L, TOKEN));
    }


    @Test
    void getTrainingsCount_shouldReturnCount() {

        when(trainingRepository.count()).thenReturn(5L);

        Long result = trainingService.getTrainingsCount();

        assertEquals(5L, result);
    }


    @Test
    void getTrainingsByTrainee_shouldReturnList() {

        GetTraineeTrainingsCriteriaFilterDTO dto =
                new GetTraineeTrainingsCriteriaFilterDTO();

        Training training = new Training();

        when(trainingRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(training));

        when(trainingMapperI.toTraineeTrainingResponseDTO(training))
                .thenReturn(new TraineeTrainingResponseDTO());

        List<TraineeTrainingResponseDTO> result =
                trainingService.getTrainingsByTraineeUsernameCriteria(dto);

        assertEquals(1, result.size());
    }


    @Test
    void getTrainingsByTrainer_shouldReturnList() {

        GetTrainerTrainingsCriteriaFilterDTO dto =
                new GetTrainerTrainingsCriteriaFilterDTO();

        Training training = new Training();

        when(trainingRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(training));

        when(trainingMapperI.toTrainerTrainingResponseDTO(training))
                .thenReturn(new TrainerTrainingResponseDTO());

        List<TrainerTrainingResponseDTO> result =
                trainingService.getTrainingsByTrainerUsernameCriteria(dto);

        assertEquals(1, result.size());
    }
}