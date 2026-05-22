package com.epam.gym.component.steps;

import com.epam.gym.config.security.CustomUserDetails;
import com.epam.gym.dto.*;
import com.epam.gym.entity.Trainer;
import com.epam.gym.entity.User;
import com.epam.gym.enums.UserRoleEnum;
import com.epam.gym.exceptions.UserNotFoundException;
import com.epam.gym.mapper.trainer.TrainerMapperI;
import com.epam.gym.repository.TrainerRepository;
import com.epam.gym.service.TrainerService;
import com.epam.gym.service.UserRoleService;
import com.epam.gym.service.UserService;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TrainerServiceStepDefinition {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserService userService;

    @Mock
    private TrainerMapperI trainerMapper;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private TrainerService trainerService;

    private CreateTrainerRequestDTO createRequest;
    private UpdateTrainerRequestDTO updateRequest;
    private ChangeStatusRequestDTO statusRequest;
    private UserChangePasswordRequestDTO passwordRequest;

    private Trainer trainer;
    private TrainerDTO trainerDTO;
    private AuthDTO authDTO;

    private List<TrainerDTO> trainerDTOList;

    private Exception exception;

    @Before
    public void setup() {

        MockitoAnnotations.openMocks(this);

        SecurityContextHolder.clearContext();

        trainerService = new TrainerService(
                trainerRepository,
                userService,
                trainerMapper,
                userRoleService,
                passwordEncoder
        );
    }

    @Given("a valid create trainer request")
    public void a_valid_create_trainer_request() {

        createRequest = new CreateTrainerRequestDTO();

        trainer = new Trainer();

        User user = new User();
        trainer.setUser(user);

        when(userService.generateUsername(any(), any()))
                .thenReturn("trainer.john");

        when(userService.generatePassword())
                .thenReturn("password");

        when(passwordEncoder.encode(any()))
                .thenReturn("encoded-password");

        when(trainerMapper.toEntity(any()))
                .thenReturn(trainer);
    }

    @When("the trainer profile is created")
    public void the_trainer_profile_is_created() {
        authDTO = trainerService.createTrainer(createRequest);
    }

    @Then("trainer credentials should be returned")
    public void trainer_credentials_should_be_returned() {

        Assertions.assertEquals(
                "trainer.john",
                authDTO.getUsername()
        );

        Assertions.assertEquals(
                "password",
                authDTO.getPassword()
        );
    }

    @And("trainer should be saved")
    public void trainer_should_be_saved() {
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Given("an existing trainer")
    public void an_existing_trainer() {

        trainer = new Trainer();

        User user = new User();
        user.setUsername("trainer");

        trainer.setUser(user);

        CustomUserDetails customUserDetails =
                new CustomUserDetails(
                        user,
                        List.of(UserRoleEnum.ROLE_TRAINER)
                );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        customUserDetails,
                        null,
                        customUserDetails.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(trainerRepository.findByUserUsername("trainer"))
                .thenReturn(Optional.of(trainer));

        when(trainerMapper.toTrainerDTO(any()))
                .thenReturn(new TrainerDTO());
    }

    @When("trainer profile is requested")
    public void trainer_profile_is_requested() {

        try {
            trainerDTO = trainerService.getTrainerByUsername();
        } catch (Exception ex) {
            exception = ex;
        }
    }

    @Then("trainer details should be returned")
    public void trainer_details_should_be_returned() {
        Assertions.assertNotNull(trainerDTO);
    }

    @Given("trainer username does not exist")
    public void trainer_username_does_not_exist() {

        User user = new User();
        user.setUsername("trainer");

        CustomUserDetails customUserDetails =
                new CustomUserDetails(
                        user,
                        List.of(UserRoleEnum.ROLE_TRAINER)
                );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        customUserDetails,
                        null,
                        customUserDetails.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(trainerRepository.findByUserUsername("trainer"))
                .thenReturn(Optional.empty());
    }

    @Then("trainer not found exception should be thrown")
    public void trainer_not_found_exception_should_be_thrown() {
        Assertions.assertTrue(
                exception instanceof UserNotFoundException
        );
    }

    @Given("a valid trainer password change request")
    public void a_valid_trainer_password_change_request() {
        passwordRequest = new UserChangePasswordRequestDTO();
    }

    @When("trainer password is changed")
    public void trainer_password_is_changed() {
        trainerService.changePassword(passwordRequest);
    }

    @Then("trainer password should be updated")
    public void trainer_password_should_be_updated() {
        verify(userService).changePassword(passwordRequest);
    }

    @Given("a valid trainer status request")
    public void a_valid_trainer_status_request() {
        statusRequest = new ChangeStatusRequestDTO();
    }

    @When("trainer status is changed")
    public void trainer_status_is_changed() {
        trainerService.changeStatusTrainee(statusRequest);
    }

    @Then("trainer status should be updated")
    public void trainer_status_should_be_updated() {
        verify(userService).changeStatus(statusRequest);
    }

    @Given("an existing trainer for update")
    public void an_existing_trainer_for_update() {

        updateRequest = new UpdateTrainerRequestDTO();

        trainer = new Trainer();

        User user = new User();
        user.setUsername("trainer");

        trainer.setUser(user);

        CustomUserDetails customUserDetails =
                new CustomUserDetails(
                        user,
                        List.of(UserRoleEnum.ROLE_TRAINER)
                );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        customUserDetails,
                        null,
                        customUserDetails.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(trainerRepository.findByUserUsername("trainer"))
                .thenReturn(Optional.of(trainer));

        when(trainerMapper.toTrainerDTO(any()))
                .thenReturn(new TrainerDTO());

        when(trainerRepository.save(any()))
                .thenReturn(trainer);
    }

    @When("trainer profile is updated")
    public void trainer_profile_is_updated() {

        try {
            trainerDTO = trainerService.updateTrainer(updateRequest);
        } catch (Exception ex) {
            exception = ex;
        }
    }

    @Then("updated trainer should be returned")
    public void updated_trainer_should_be_returned() {

        Assertions.assertNotNull(trainerDTO);

        verify(trainerMapper)
                .updateTrainerFromDto(updateRequest, trainer);

        verify(trainerRepository)
                .save(trainer);
    }

    @Given("trainers not assigned to trainee")
    public void trainers_not_assigned_to_trainee() {

        trainer = new Trainer();

        when(trainerRepository.findTrainersNotAssignedToTrainee("john"))
                .thenReturn(List.of(trainer));

        when(trainerMapper.toTrainerDTO(any()))
                .thenReturn(new TrainerDTO());
    }

    @When("trainers not assigned are requested")
    public void trainers_not_assigned_are_requested() {

        trainerDTOList =
                trainerService.getTrainersNotAssignedOnTrainee("john");
    }

    @Then("unassigned trainers should be returned")
    public void unassigned_trainers_should_be_returned() {

        Assertions.assertNotNull(trainerDTOList);

        Assertions.assertEquals(1, trainerDTOList.size());
    }
}