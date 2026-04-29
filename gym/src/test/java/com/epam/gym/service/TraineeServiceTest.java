package com.epam.gym.service;

import com.epam.gym.dto.*;
import com.epam.gym.entity.Trainee;
import com.epam.gym.entity.Trainer;
import com.epam.gym.entity.User;
import com.epam.gym.enums.UserRoleEnum;
import com.epam.gym.exceptions.UserNotFoundException;
import com.epam.gym.mapper.trainee.TraineeMapperI;
import com.epam.gym.mapper.trainer.TrainerMapperI;
import com.epam.gym.repository.TraineeRepository;
import com.epam.gym.util.SpringSecurityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TraineeMapperI traineeMapperI;

    @Mock
    private TrainerMapperI trainerMapperI;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TraineeService traineeService;

    @Test
    void changePassword_shouldDelegateToUserService() {
        UserChangePasswordRequestDTO dto = new UserChangePasswordRequestDTO();

        traineeService.changePassword(dto);

        verify(userService).changePassword(dto);
    }

    @Test
    void getTrainee_shouldReturnTrainee_whenExists() {
        Trainee trainee = new Trainee();

        when(traineeRepository.findByUserUsername("john"))
                .thenReturn(Optional.of(trainee));

        Trainee result = traineeService.getTrainee("john");

        assertNotNull(result);
    }

    @Test
    void deleteTrainee_shouldRemoveTraineeFromTrainers() {
        Trainee trainee = new Trainee();

        Trainer trainer = new Trainer();
        trainer.setTrainees(new HashSet<>());

        trainee.setTrainers(new HashSet<>(List.of(trainer)));
        trainer.getTrainees().add(trainee);

        when(traineeRepository.findByUserUsername("john"))
                .thenReturn(Optional.of(trainee));

        traineeService.deleteTrainee("john");

        assertFalse(trainer.getTrainees().contains(trainee));
        assertTrue(trainee.getTrainers().isEmpty());

        verify(traineeRepository).delete(trainee);
    }

    @Test
    void updateTrainerList_shouldRemoveOldTrainers_andAddNewOnes() {
        Trainee trainee = new Trainee();

        Trainer oldTrainer = new Trainer();
        oldTrainer.setTrainees(new HashSet<>());

        trainee.setTrainers(new HashSet<>(List.of(oldTrainer)));

        Trainer newTrainer = new Trainer();
        newTrainer.setTrainees(new HashSet<>());

        UpdateTrainersRequestDTO dto = new UpdateTrainersRequestDTO();
        dto.setTrainerUsernames(List.of("newTrainer"));

        when(traineeRepository.findByUserUsername("john"))
                .thenReturn(Optional.of(trainee));

        when(trainerService.getTrainersByUsernames(any()))
                .thenReturn(List.of(newTrainer));

        when(trainerMapperI.toTrainerDTO(any()))
                .thenReturn(new TrainerDTO());

        List<TrainerDTO> result =
                traineeService.updateTrainerList("john", dto);

        assertEquals(1, result.size());
        assertTrue(trainee.getTrainers().contains(newTrainer));

        verify(traineeRepository).save(trainee);
    }

    @Test
    void updateTrainerList_shouldHandleEmptyTrainerList() {
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

    @Test
    void createTrainee_shouldSetUserFieldsCorrectly() {
        CreateTraineeRequestDTO dto = new CreateTraineeRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");

        when(userService.generateUsername(any(), any()))
                .thenReturn("john");

        when(userService.generatePassword())
                .thenReturn("pass");

        when(passwordEncoder.encode("pass"))
                .thenReturn("encoded");

        Trainee trainee = new Trainee();
        trainee.setUser(new User());

        when(traineeMapperI.toTrainee(any()))
                .thenReturn(trainee);

        traineeService.createTrainee(dto);

        assertEquals("john", trainee.getUser().getUsername());
        assertEquals("encoded", trainee.getUser().getPassword());
        assertTrue(trainee.getUser().getIsActive());
    }

    @Test
    void createTrainee_shouldSaveTrainee_andReturnAuthDTO() {

        CreateTraineeRequestDTO dto = new CreateTraineeRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");

        when(userService.generateUsername("John", "Doe"))
                .thenReturn("john.doe");

        when(userService.generatePassword())
                .thenReturn("pass123");

        when(passwordEncoder.encode("pass123"))
                .thenReturn("encoded-pass");

        when(traineeMapperI.toTrainee(any()))
                .thenAnswer(invocation -> {
                    Trainee t = new Trainee();
                    t.setUser(new User());
                    return t;
                });

        AuthDTO response = traineeService.createTrainee(dto);

        assertEquals("john.doe", response.getUsername());
        assertEquals("pass123", response.getPassword());

        verify(traineeRepository).save(any());
        verify(userRoleService)
                .merge(any(), eq(List.of(UserRoleEnum.ROLE_TRAINEE)));
    }


    @Test
    void getTraineeByUsername_shouldReturnDTO() {

        User user = new User();
        user.setUsername("john");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        try (var mocked = mockStatic(SpringSecurityUtil.class)) {

            mocked.when(SpringSecurityUtil::getCurrentUser)
                    .thenReturn(user);

            when(traineeRepository.findByUserUsername("john"))
                    .thenReturn(Optional.of(trainee));

            when(traineeMapperI.toTraineeDTO(any()))
                    .thenReturn(new TraineeDTO());

            TraineeDTO result = traineeService.getTraineeByUsername();

            assertNotNull(result);
        }
    }

    @Test
    void getTrainee_shouldThrowException_whenNotFound() {

        when(traineeRepository.findByUserUsername("john"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> traineeService.getTrainee("john"));
    }


    @Test
    void updateTrainee_shouldUpdateAndSave() {

        User user = new User();
        user.setUsername("john");

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        UpdateTraineeRequestDTO dto = new UpdateTraineeRequestDTO();
        dto.setFirstName("New");

        try (var mocked = mockStatic(SpringSecurityUtil.class)) {

            mocked.when(SpringSecurityUtil::getCurrentUser)
                    .thenReturn(user);

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


    @Test
    void deleteTrainee_shouldDeleteTrainee() {

        Trainee trainee = new Trainee();
        trainee.setTrainers(new HashSet<>());

        when(traineeRepository.findByUserUsername("john"))
                .thenReturn(Optional.of(trainee));

        traineeService.deleteTrainee("john");

        verify(traineeRepository).delete(trainee);
    }


    @Test
    void changeStatusTrainee_shouldDelegate() {

        ChangeStatusRequestDTO dto = new ChangeStatusRequestDTO();

        traineeService.changeStatusTrainee(dto);

        verify(userService).changeStatus(dto);
    }


    @Test
    void updateTrainerList_shouldReplaceTrainers() {

        Trainee trainee = new Trainee();
        trainee.setTrainers(new HashSet<>());

        Trainer trainer = new Trainer();
        trainer.setUser(new User());

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
        verify(traineeRepository).save(trainee);
    }
}