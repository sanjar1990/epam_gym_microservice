package com.epam.gym.component.steps;

import com.epam.gym.config.security.CustomUserDetails;
import com.epam.gym.dto.*;
import com.epam.gym.entity.Trainee;
import com.epam.gym.entity.Trainer;
import com.epam.gym.entity.Training;
import com.epam.gym.entity.User;
import com.epam.gym.enums.UserRoleEnum;
import com.epam.gym.exceptions.UserNotFoundException;
import com.epam.gym.mapper.trainee.TraineeMapperI;
import com.epam.gym.mapper.trainer.TrainerMapperI;
import com.epam.gym.repository.TraineeRepository;
import com.epam.gym.service.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TraineeServiceStepDefinition {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TraineeMapperI traineeMapper;

    @Mock
    private TrainerMapperI trainerMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TrainingService trainingService;

    private TraineeService traineeService;

    private CreateTraineeRequestDTO createRequest;
    private UpdateTraineeRequestDTO updateRequest;
    private ChangeStatusRequestDTO statusRequest;
    private UserChangePasswordRequestDTO passwordRequest;
    private UpdateTrainersRequestDTO updateTrainersRequest;

    private AuthDTO authDTO;
    private TraineeDTO traineeDTO;

    private Exception exception;

    private Trainee trainee;
    private List<TrainerDTO> trainerDTOList;
    private Trainer trainer;
    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        SecurityContextHolder.clearContext();

        traineeService = new TraineeService(
                traineeRepository,
                userService,
                trainerService,
                traineeMapper,
                trainerMapper,
                userRoleService,
                passwordEncoder,
                trainingService
        );
    }

    @Given("a valid create trainee request")
    public void a_valid_create_trainee_request() {

        createRequest = new CreateTraineeRequestDTO();

        trainee = new Trainee();

        User user = new User();
        trainee.setUser(user);

        when(userService.generateUsername(any(), any()))
                .thenReturn("john.doe");

        when(userService.generatePassword())
                .thenReturn("password");

        when(passwordEncoder.encode(any()))
                .thenReturn("encoded-password");

        when(traineeMapper.toTrainee(any()))
                .thenReturn(trainee);
    }

    @When("the trainee profile is created")
    public void the_trainee_profile_is_created() {
        authDTO = traineeService.createTrainee(createRequest);
    }

    @Then("trainee credentials should be returned")
    public void trainee_credentials_should_be_returned() {
        Assertions.assertEquals("john.doe", authDTO.getUsername());
        Assertions.assertEquals("password", authDTO.getPassword());
    }

    @And("trainee should be saved")
    public void trainee_should_be_saved() {
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Given("an existing trainee")
    public void an_existing_trainee() {

        trainee = new Trainee();

        User user = new User();
        user.setUsername("john");

        trainee.setUser(user);

        CustomUserDetails customUserDetails =
                new CustomUserDetails(user, List.of(UserRoleEnum.ROLE_TRAINEE));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        customUserDetails,
                        null,
                        customUserDetails.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(traineeRepository.findByUserUsername("john"))
                .thenReturn(Optional.of(trainee));

        TraineeDTO dto = new TraineeDTO();

        when(traineeMapper.toTraineeDTO(any(Trainee.class)))
                .thenReturn(dto);
    }
    @When("trainee profile is requested")
    public void trainee_profile_is_requested() {

        try {
            traineeDTO = traineeService.getTraineeByUsername();
        } catch (Exception ex) {
            exception = ex;
        }
    }

    @Then("trainee details should be returned")
    public void trainee_details_should_be_returned() {
        Assertions.assertNotNull(traineeDTO);
    }

    @Given("trainee username does not exist")
    public void trainee_username_does_not_exist() {

        User user = new User();
        user.setUsername("john");

        CustomUserDetails customUserDetails =
                new CustomUserDetails(user, List.of(UserRoleEnum.ROLE_TRAINEE));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        customUserDetails,
                        null,
                        customUserDetails.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(traineeRepository.findByUserUsername("john"))
                .thenReturn(Optional.empty());
    }

    @Then("trainee not found exception should be thrown")
    public void trainee_not_found_exception_should_be_thrown() {
        Assertions.assertTrue(exception instanceof UserNotFoundException);
    }

    @Given("a valid password change request")
    public void a_valid_password_change_request() {
        passwordRequest = new UserChangePasswordRequestDTO();
    }

    @When("trainee password is changed")
    public void trainee_password_is_changed() {
        traineeService.changePassword(passwordRequest);
    }

    @Then("password should be updated")
    public void password_should_be_updated() {
        verify(userService).changePassword(passwordRequest);
    }

    @Given("a valid change status request")
    public void a_valid_change_status_request() {
        statusRequest = new ChangeStatusRequestDTO();
    }

    @When("trainee status is changed")
    public void trainee_status_is_changed() {
        traineeService.changeStatusTrainee(statusRequest);
    }

    @Then("trainee status should be updated")
    public void trainee_status_should_be_updated() {
        verify(userService).changeStatus(statusRequest);
    }

    @Given("an existing trainee with trainers and trainings")
    public void an_existing_trainee_with_trainers_and_trainings() {

        trainee = new Trainee();

        trainee.setTrainers(new HashSet<>(Set.of(new Trainer())));
        trainee.setTrainings(new ArrayList<>(List.of(new Training())));

        when(traineeRepository.findByUserUsername("john"))
                .thenReturn(Optional.of(trainee));
    }

    @When("trainee profile is deleted")
    public void trainee_profile_is_deleted() {
        traineeService.deleteTrainee("john", "token");
    }

    @Then("trainee should be removed")
    public void trainee_should_be_removed() {
        verify(traineeRepository).delete(any(Trainee.class));
    }

    @Given("an existing trainee for update")
    public void an_existing_trainee_for_update() {

        updateRequest = new UpdateTraineeRequestDTO();

        trainee = new Trainee();

        User user = new User();
        user.setUsername("john");

        trainee.setUser(user);

        CustomUserDetails customUserDetails =
                new CustomUserDetails(
                        user,
                        List.of(UserRoleEnum.ROLE_TRAINEE)
                );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        customUserDetails,
                        null,
                        customUserDetails.getAuthorities()
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(traineeRepository.findByUserUsername("john"))
                .thenReturn(Optional.of(trainee));

        when(traineeRepository.save(any(Trainee.class)))
                .thenReturn(trainee);

        when(traineeMapper.toTraineeDTO(any()))
                .thenReturn(new TraineeDTO());
    }

    @When("trainee profile is updated")
    public void trainee_profile_is_updated() {

        try {
            traineeDTO = traineeService.updateTrainee(updateRequest);
        } catch (Exception ex) {
            ex.printStackTrace();
            exception = ex;
        }
    }
    @Then("updated trainee should be returned")
    public void updated_trainee_should_be_returned() {

        Assertions.assertNotNull(traineeDTO);

        verify(traineeMapper, times(1))
                .updateTraineeFromDto(updateRequest, trainee);

        verify(traineeRepository, times(1))
                .save(trainee);
    }

    @Given("an existing trainee and trainers")
    public void an_existing_trainee_and_trainers() {

        updateTrainersRequest = new UpdateTrainersRequestDTO();
        updateTrainersRequest.setTrainerUsernames(
                List.of("trainer1", "trainer2")
        );

        trainee = new Trainee();

        User traineeUser = new User();
        traineeUser.setUsername("john");

        trainee.setUser(traineeUser);

        trainer = new Trainer();

        User trainerUser = new User();
        trainerUser.setUsername("trainer1");

        trainer.setUser(trainerUser);

        when(traineeRepository.findByUserUsername("john"))
                .thenReturn(Optional.of(trainee));

        when(trainerService.getTrainersByUsernames(any()))
                .thenReturn(List.of(trainer));

        when(trainerMapper.toTrainerDTO(any()))
                .thenReturn(new TrainerDTO());

        when(traineeRepository.save(any(Trainee.class)))
                .thenReturn(trainee);
    }

    @When("trainee trainer list is updated")
    public void trainee_trainer_list_is_updated() {

        try {
            trainerDTOList = traineeService.updateTrainerList(
                    "john",
                    updateTrainersRequest
            );
        } catch (Exception ex) {
            exception = ex;
        }
    }

    @Then("updated trainer list should be returned")
    public void updated_trainer_list_should_be_returned() {

        Assertions.assertNotNull(trainerDTOList);

        verify(trainerService, times(1))
                .getTrainersByUsernames(
                        updateTrainersRequest.getTrainerUsernames()
                );

        verify(traineeRepository, times(1))
                .save(trainee);
    }
}