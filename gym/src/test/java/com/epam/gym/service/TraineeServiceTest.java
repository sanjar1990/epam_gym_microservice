package com.epam.gym.service;

import com.epam.gym.dto.*;
import com.epam.gym.entity.*;
import com.epam.gym.enums.UserRoleEnum;
import com.epam.gym.exceptions.UserNotFoundException;
import com.epam.gym.mapper.trainee.TraineeMapperI;
import com.epam.gym.mapper.trainer.TrainerMapperI;
import com.epam.gym.repository.TraineeRepository;
import com.epam.gym.util.SpringSecurityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock private TraineeRepository traineeRepository;
    @Mock private UserService userService;
    @Mock private UserRoleService userRoleService;
    @Mock private TrainerService trainerService;
    @Mock private TraineeMapperI traineeMapperI;
    @Mock private TrainerMapperI trainerMapperI;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private TrainingService trainingService;

    @InjectMocks
    private TraineeService traineeService;

    // ---------------- CREATE ----------------

    @Test
    void createTrainee_shouldCreateAndReturnAuthDTO() {
        CreateTraineeRequestDTO dto = new CreateTraineeRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");

        when(userService.generateUsername("John", "Doe"))
                .thenReturn("john.doe");
        when(userService.generatePassword())
                .thenReturn("pass123");
        when(passwordEncoder.encode("pass123"))
                .thenReturn("encoded");

        Trainee trainee = new Trainee();
        trainee.setUser(new User());

        when(traineeMapperI.toTrainee(any())).thenReturn(trainee);

        AuthDTO result = traineeService.createTrainee(dto);

        assertEquals("john.doe", result.getUsername());
        assertEquals("pass123", result.getPassword());

        assertEquals("john.doe", trainee.getUser().getUsername());
        assertEquals("encoded", trainee.getUser().getPassword());
        assertTrue(trainee.getUser().getIsActive());

        verify(traineeRepository).save(trainee);
        verify(userRoleService)
                .merge(any(), eq(List.of(UserRoleEnum.ROLE_TRAINEE)));
    }

    // ---------------- GET ----------------

    @Test
    void getTrainee_shouldReturnTrainee() {
        Trainee trainee = new Trainee();

        when(traineeRepository.findByUserUsername("john"))
                .thenReturn(Optional.of(trainee));

        Trainee result = traineeService.getTrainee("john");

        assertNotNull(result);
    }

    @Test
    void getTrainee_shouldThrow_whenNotFound() {
        when(traineeRepository.findByUserUsername("john"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> traineeService.getTrainee("john"));
    }

    @Test
    void getTraineeByUsername_shouldReturnDTO() {
        User user = new User();
        user.setUsername("john");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        try (MockedStatic<SpringSecurityUtil> mocked = mockStatic(SpringSecurityUtil.class)) {

            mocked.when(SpringSecurityUtil::getCurrentUser).thenReturn(user);

            when(traineeRepository.findByUserUsername("john"))
                    .thenReturn(Optional.of(trainee));

            when(traineeMapperI.toTraineeDTO(any()))
                    .thenReturn(new TraineeDTO());

            TraineeDTO result = traineeService.getTraineeByUsername();

            assertNotNull(result);
        }
    }

    // ---------------- UPDATE ----------------

    @Test
    void updateTrainee_shouldUpdateAndSave() {
        User user = new User();
        user.setUsername("john");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        UpdateTraineeRequestDTO dto = new UpdateTraineeRequestDTO();

        try (MockedStatic<SpringSecurityUtil> mocked = mockStatic(SpringSecurityUtil.class)) {

            mocked.when(SpringSecurityUtil::getCurrentUser).thenReturn(user);

            when(traineeRepository.findByUserUsername("john"))
                    .thenReturn(Optional.of(trainee));

            when(traineeRepository.save(any()))
                    .thenReturn(trainee);

            doNothing().when(traineeMapperI)
                    .updateTraineeFromDto(any(), any());

            when(traineeMapperI.toTraineeDTO(any()))
                    .thenReturn(new TraineeDTO());

            TraineeDTO result = traineeService.updateTrainee(dto);

            assertNotNull(result);
            verify(traineeRepository).save(trainee);
        }
    }

    // ---------------- DELETE ----------------

    @Test
    void deleteTrainee_shouldDeleteTrainingsAndRelations() {

        Training t1 = new Training();
        Training t2 = new Training();

        Trainee trainee = new Trainee();
        trainee.setTrainings(new ArrayList<>(List.of(t1, t2)));

        Trainer trainer = new Trainer();
        trainer.setTrainees(new HashSet<>());
        trainer.getTrainees().add(trainee);

        trainee.setTrainers(new HashSet<>(List.of(trainer)));

        when(traineeRepository.findByUserUsername("john"))
                .thenReturn(Optional.of(trainee));

        traineeService.deleteTrainee("john", "Bearer token");

        // verify trainings deleted
        verify(trainingService, times(2))
                .deleteTraining(any(Training.class), eq("Bearer token"));

        // relation cleanup
        assertFalse(trainer.getTrainees().contains(trainee));
        assertTrue(trainee.getTrainers().isEmpty());

        verify(traineeRepository).delete(trainee);
    }

    // ---------------- PASSWORD ----------------

    @Test
    void changePassword_shouldDelegate() {
        UserChangePasswordRequestDTO dto = new UserChangePasswordRequestDTO();

        traineeService.changePassword(dto);

        verify(userService).changePassword(dto);
    }

    // ---------------- STATUS ----------------

    @Test
    void changeStatusTrainee_shouldDelegate() {
        ChangeStatusRequestDTO dto = new ChangeStatusRequestDTO();

        traineeService.changeStatusTrainee(dto);

        verify(userService).changeStatus(dto);
    }

    // ---------------- TRAINER LIST ----------------

    @Test
    void updateTrainerList_shouldReplaceTrainers() {

        Trainee trainee = new Trainee();
        trainee.setTrainers(new HashSet<>());

        Trainer trainer = new Trainer();
        trainer.setTrainees(new HashSet<>());

        UpdateTrainersRequestDTO dto = new UpdateTrainersRequestDTO();
        dto.setTrainerUsernames(List.of("trainer1"));

        when(traineeRepository.findByUserUsername("john"))
                .thenReturn(Optional.of(trainee));

        when(trainerService.getTrainersByUsernames(any()))
                .thenReturn(List.of(trainer));

        when(trainerMapperI.toTrainerDTO(any()))
                .thenReturn(new TrainerDTO());

        List<TrainerDTO> result =
                traineeService.updateTrainerList("john", dto);

        assertEquals(1, result.size());
        assertTrue(trainee.getTrainers().contains(trainer));

        verify(traineeRepository).save(trainee);
    }

    @Test
    void updateTrainerList_shouldHandleEmptyList() {

        Trainee trainee = new Trainee();
        trainee.setTrainers(new HashSet<>());

        UpdateTrainersRequestDTO dto = new UpdateTrainersRequestDTO();
        dto.setTrainerUsernames(List.of());

        when(traineeRepository.findByUserUsername("john"))
                .thenReturn(Optional.of(trainee));

        when(trainerService.getTrainersByUsernames(any()))
                .thenReturn(List.of());

        List<TrainerDTO> result =
                traineeService.updateTrainerList("john", dto);

        assertTrue(result.isEmpty());
    }
}