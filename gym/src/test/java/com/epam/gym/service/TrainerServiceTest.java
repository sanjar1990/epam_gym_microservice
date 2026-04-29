package com.epam.gym.service;

import com.epam.gym.dto.*;
import com.epam.gym.entity.Trainer;
import com.epam.gym.entity.User;
import com.epam.gym.enums.UserRoleEnum;
import com.epam.gym.exceptions.UserNotFoundException;
import com.epam.gym.mapper.trainer.TrainerMapperI;
import com.epam.gym.repository.TrainerRepository;
import com.epam.gym.util.SpringSecurityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserService userService;

    @Mock
    private TrainerMapperI trainerMapperI;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TrainerService trainerService;

    @Test
    void updateTrainer_shouldSaveOnce() {
        User user = new User();
        user.setUsername("john");

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        try (MockedStatic<SpringSecurityUtil> mocked = mockStatic(SpringSecurityUtil.class)) {

            mocked.when(SpringSecurityUtil::getCurrentUser)
                    .thenReturn(user);

            when(trainerRepository.findByUserUsername("john"))
                    .thenReturn(Optional.of(trainer));

            when(trainerMapperI.toTrainerDTO(any()))
                    .thenReturn(new TrainerDTO());

            trainerService.updateTrainer(new UpdateTrainerRequestDTO());

            verify(trainerRepository, times(1)).save(trainer);
        }
    }

    @Test
    void getTrainersByUsernames_shouldHandleEmptyInput() {
        when(trainerRepository.findByUserUsernameIn(List.of()))
                .thenReturn(List.of());

        List<Trainer> result =
                trainerService.getTrainersByUsernames(List.of());

        assertTrue(result.isEmpty());
    }

    @Test
    void getTrainersNotAssignedOnTrainee_shouldReturnEmptyList() {
        when(trainerRepository.findTrainersNotAssignedToTrainee("john"))
                .thenReturn(List.of());

        List<TrainerDTO> result =
                trainerService.getTrainersNotAssignedOnTrainee("john");

        assertTrue(result.isEmpty());
    }

    @Test
    void updateTrainer_shouldCallMapperUpdateMethod() {
        User user = new User();
        user.setUsername("john");

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        UpdateTrainerRequestDTO dto = new UpdateTrainerRequestDTO();

        try (MockedStatic<SpringSecurityUtil> mocked = mockStatic(SpringSecurityUtil.class)) {

            mocked.when(SpringSecurityUtil::getCurrentUser)
                    .thenReturn(user);

            when(trainerRepository.findByUserUsername("john"))
                    .thenReturn(Optional.of(trainer));

            when(trainerMapperI.toTrainerDTO(any()))
                    .thenReturn(new TrainerDTO());

            trainerService.updateTrainer(dto);

            verify(trainerMapperI).updateTrainerFromDto(dto, trainer);
        }
    }

    @Test
    void getTrainerByUsername_shouldCallMapper() {
        User user = new User();
        user.setUsername("john");

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        try (MockedStatic<SpringSecurityUtil> mocked = mockStatic(SpringSecurityUtil.class)) {

            mocked.when(SpringSecurityUtil::getCurrentUser)
                    .thenReturn(user);

            when(trainerRepository.findByUserUsername("john"))
                    .thenReturn(Optional.of(trainer));

            trainerService.getTrainerByUsername();

            verify(trainerMapperI).toTrainerDTO(trainer);
        }
    }

    @Test
    void createTrainer_shouldSetUserFieldsCorrectly() {
        CreateTrainerRequestDTO dto = new CreateTrainerRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Smith");

        when(userService.generateUsername(any(), any()))
                .thenReturn("john");

        when(userService.generatePassword())
                .thenReturn("pass");

        when(passwordEncoder.encode("pass"))
                .thenReturn("encoded");

        Trainer trainer = new Trainer();
        trainer.setUser(new User());

        when(trainerMapperI.toEntity(any()))
                .thenReturn(trainer);

        trainerService.createTrainer(dto);

        assertEquals("john", trainer.getUser().getUsername());
        assertEquals("encoded", trainer.getUser().getPassword());
        assertTrue(trainer.getUser().getIsActive());
    }

    @Test
    void createTrainer_shouldAssignCorrectRoles() {
        CreateTrainerRequestDTO dto = new CreateTrainerRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Smith");

        when(userService.generateUsername(any(), any()))
                .thenReturn("john");

        when(userService.generatePassword())
                .thenReturn("pass");

        when(passwordEncoder.encode(any()))
                .thenReturn("encoded");

        Trainer trainer = new Trainer();
        trainer.setUser(new User());

        when(trainerMapperI.toEntity(any()))
                .thenReturn(trainer);

        trainerService.createTrainer(dto);

        verify(userRoleService).merge(
                any(),
                eq(List.of(UserRoleEnum.ROLE_TRAINER, UserRoleEnum.ROLE_ADMIN))
        );
    }

    @Test
    void createTrainer_shouldCreateTrainer_andReturnAuthDTO() {
        CreateTrainerRequestDTO dto = new CreateTrainerRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Smith");

        when(userService.generateUsername("John", "Smith"))
                .thenReturn("john.smith");

        when(userService.generatePassword())
                .thenReturn("pass123");

        when(passwordEncoder.encode("pass123"))
                .thenReturn("encodedPass");

        Trainer trainer = new Trainer();
        trainer.setUser(new User());

        when(trainerMapperI.toEntity(any()))
                .thenReturn(trainer);

        AuthDTO response = trainerService.createTrainer(dto);

        assertNotNull(response);
        assertEquals("john.smith", response.getUsername());
        assertEquals("pass123", response.getPassword());

        verify(trainerRepository).save(trainer);
        verify(userRoleService)
                .merge(any(), any());
    }

    @Test
    void getTrainerByUsername_shouldReturnDTO() {
        User user = new User();
        user.setUsername("john");

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        try (MockedStatic<SpringSecurityUtil> mocked = mockStatic(SpringSecurityUtil.class)) {

            mocked.when(SpringSecurityUtil::getCurrentUser)
                    .thenReturn(user);

            when(trainerRepository.findByUserUsername("john"))
                    .thenReturn(Optional.of(trainer));

            when(trainerMapperI.toTrainerDTO(trainer))
                    .thenReturn(new TrainerDTO());

            TrainerDTO result = trainerService.getTrainerByUsername();

            assertNotNull(result);
        }
    }

    @Test
    void changePassword_shouldCallUserService() {
        UserChangePasswordRequestDTO dto = new UserChangePasswordRequestDTO();
        dto.setUsername("john");

        trainerService.changePassword(dto);

        verify(userService).changePassword(dto);
    }


    @Test
    void updateTrainer_shouldUpdateTrainer() {
        User user = new User();
        user.setUsername("john");

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        UpdateTrainerRequestDTO dto = new UpdateTrainerRequestDTO();
        dto.setFirstName("New");

        try (MockedStatic<SpringSecurityUtil> mocked = mockStatic(SpringSecurityUtil.class)) {

            mocked.when(SpringSecurityUtil::getCurrentUser)
                    .thenReturn(user);

            when(trainerRepository.findByUserUsername("john"))
                    .thenReturn(Optional.of(trainer));

            doNothing().when(trainerMapperI)
                    .updateTrainerFromDto(dto, trainer);

            when(trainerMapperI.toTrainerDTO(trainer))
                    .thenReturn(new TrainerDTO());

            TrainerDTO result = trainerService.updateTrainer(dto);

            assertNotNull(result);

            verify(trainerRepository).save(trainer);
        }
    }

    @Test
    void changeStatusTrainee_shouldCallUserService() {
        ChangeStatusRequestDTO dto = new ChangeStatusRequestDTO();
        dto.setUsername("john");

        trainerService.changeStatusTrainee(dto);

        verify(userService).changeStatus(dto);
    }

    @Test
    void getTrainersNotAssignedOnTrainee_shouldReturnList() {
        Trainer trainer = new Trainer();

        when(trainerRepository.findTrainersNotAssignedToTrainee("john"))
                .thenReturn(List.of(trainer));

        when(trainerMapperI.toTrainerDTO(trainer))
                .thenReturn(new TrainerDTO());

        List<TrainerDTO> result =
                trainerService.getTrainersNotAssignedOnTrainee("john");

        assertEquals(1, result.size());
    }

    @Test
    void getTrainerEntityByUsername_shouldReturnTrainer() {
        Trainer trainer = new Trainer();

        when(trainerRepository.findByUserUsername("john"))
                .thenReturn(Optional.of(trainer));

        Trainer result =
                trainerService.getTrainerEntityByUsername("john");

        assertNotNull(result);
    }

    @Test
    void getTrainerEntityByUsername_shouldThrow() {
        when(trainerRepository.findByUserUsername("john"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> trainerService.getTrainerEntityByUsername("john"));
    }

    @Test
    void getTrainersByUsernames_shouldReturnList() {
        List<String> usernames = List.of("john");

        when(trainerRepository.findByUserUsernameIn(usernames))
                .thenReturn(List.of(new Trainer()));

        List<Trainer> result =
                trainerService.getTrainersByUsernames(usernames);

        assertEquals(1, result.size());
    }
}