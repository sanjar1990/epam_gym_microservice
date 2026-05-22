package com.epam.gym.component.steps;

import com.epam.gym.config.security.CustomUserDetails;
import com.epam.gym.dto.*;
import com.epam.gym.entity.Trainee;
import com.epam.gym.entity.Trainer;
import com.epam.gym.entity.Training;
import com.epam.gym.entity.User;
import com.epam.gym.enums.ActionType;
import com.epam.gym.enums.UserRoleEnum;
import com.epam.gym.mapper.training.TrainingMapperI;
import com.epam.gym.repository.TrainingRepository;
import com.epam.gym.service.TraineeService;
import com.epam.gym.service.TrainerService;
import com.epam.gym.service.TrainingService;
import com.epam.gym.service.WorkloadConnectionI;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TrainingServiceStepDefinition {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingMapperI trainingMapperI;

    @Mock
    private WorkloadConnectionI workloadConnection;

    private TrainingService trainingService;

    private CreateTrainingDTO createTrainingDTO;

    private Training training;
    private Trainer trainer;
    private Trainee trainee;

    private Long trainingId;

    private Exception exception;

    private List<TraineeTrainingResponseDTO> traineeTrainingResponseDTOS;
    private List<TrainerTrainingResponseDTO> trainerTrainingResponseDTOS;

    @Before
    public void setup() {

        MockitoAnnotations.openMocks(this);

        SecurityContextHolder.clearContext();

        trainingService = new TrainingService(
                trainingRepository,
                traineeService,
                trainerService,
                trainingMapperI,
                workloadConnection
        );

        MDC.put("transactionId", "transaction-id");
    }

    @Given("a valid create training request")
    public void a_valid_create_training_request() {

        createTrainingDTO = new CreateTrainingDTO();

        createTrainingDTO.setTraineeUsername("trainee");
        createTrainingDTO.setTrainingTypeId(1L);

        trainee = new Trainee();
        trainee.setTrainings(new ArrayList<>());
        trainee.setTrainers(new HashSet<>());

        trainer = new Trainer();
        trainer.setTrainees(new HashSet<>());

        User trainerUser = new User();
        trainerUser.setUsername("trainer");
        trainerUser.setFirstName("John");
        trainerUser.setLastName("Doe");
        trainerUser.setIsActive(true);

        trainer.setUser(trainerUser);

        var trainingType = new com.epam.gym.entity.TrainingType();
        trainingType.setId(1L);

        trainer.setTrainingType(trainingType);

        training = new Training();
        training.setId(1L);
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);

        User authUser = new User();
        authUser.setUsername("trainer");

        CustomUserDetails customUserDetails =
                new CustomUserDetails(
                        authUser,
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

        when(traineeService.getTrainee("trainee"))
                .thenReturn(trainee);

        when(trainerService.getTrainerEntityByUsername("trainer"))
                .thenReturn(trainer);

        when(trainingMapperI.toEntity(any()))
                .thenReturn(training);

        when(trainingRepository.save(any()))
                .thenReturn(training);
    }

    @When("training is added")
    public void training_is_added() {

        try {
            trainingId = trainingService.addTraining(
                    createTrainingDTO,
                    "token"
            );
        } catch (Exception ex) {
            exception = ex;
        }
    }

    @Then("training id should be returned")
    public void training_id_should_be_returned() {

        Assertions.assertEquals(1L, trainingId);
    }

    @And("training should be saved")
    public void training_should_be_saved() {

        verify(trainingRepository, times(1))
                .save(training);
    }

    @And("workload service should be updated")
    public void workload_service_should_be_updated() {

        verify(workloadConnection, times(1))
                .updateWorkload(
                        any(TrainerWorkloadRequest.class),
                        eq("token"),
                        eq("transaction-id")
                );
    }

    @Given("a create training request with invalid training type")
    public void a_create_training_request_with_invalid_training_type() {

        a_valid_create_training_request();

        createTrainingDTO.setTrainingTypeId(2L);
    }

    @Then("training type mismatch exception should be thrown")
    public void training_type_mismatch_exception_should_be_thrown() {

        Assertions.assertNotNull(exception);

        Assertions.assertEquals(
                "Training type id is not match",
                exception.getMessage()
        );
    }

    @Given("trainings exist for trainee criteria")
    public void trainings_exist_for_trainee_criteria() {

        Training training = new Training();

        when(trainingRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(training));

        when(trainingMapperI.toTraineeTrainingResponseDTO(any()))
                .thenReturn(new TraineeTrainingResponseDTO());
    }

    @When("trainings are requested by trainee criteria")
    public void trainings_are_requested_by_trainee_criteria() {

        GetTraineeTrainingsCriteriaFilterDTO dto =
                new GetTraineeTrainingsCriteriaFilterDTO();

        traineeTrainingResponseDTOS =
                trainingService.getTrainingsByTraineeUsernameCriteria(dto);
    }

    @Then("trainee trainings should be returned")
    public void trainee_trainings_should_be_returned() {

        Assertions.assertNotNull(traineeTrainingResponseDTOS);

        Assertions.assertEquals(
                1,
                traineeTrainingResponseDTOS.size()
        );
    }
    @Given("trainings exist for trainer criteria")
    public void trainings_exist_for_trainer_criteria() {

        Training training = new Training();

        when(trainingRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(training));

        when(trainingMapperI.toTrainerTrainingResponseDTO(any()))
                .thenReturn(new TrainerTrainingResponseDTO());
    }

    @When("trainings are requested by trainer criteria")
    public void trainings_are_requested_by_trainer_criteria() {

        GetTrainerTrainingsCriteriaFilterDTO dto =
                new GetTrainerTrainingsCriteriaFilterDTO();

        trainerTrainingResponseDTOS =
                trainingService.getTrainingsByTrainerUsernameCriteria(dto);
    }

    @Then("trainer trainings should be returned")
    public void trainer_trainings_should_be_returned() {

        Assertions.assertNotNull(trainerTrainingResponseDTOS);

        Assertions.assertEquals(
                1,
                trainerTrainingResponseDTOS.size()
        );
    }

    @Given("an existing training")
    public void an_existing_training() {

        trainer = new Trainer();

        User user = new User();
        user.setUsername("trainer");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setIsActive(true);

        trainer.setUser(user);

        training = new Training();
        training.setId(1L);
        training.setTrainer(trainer);
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60);

        when(trainingRepository.findById(1L))
                .thenReturn(Optional.of(training));
    }

    @When("training is deleted")
    public void training_is_deleted() {

        try {
            trainingService.deleteTraining(1L, "token");
        } catch (Exception ex) {
            exception = ex;
        }
    }

    @Then("training should be removed")
    public void training_should_be_removed() {

        verify(trainingRepository, times(1))
                .deleteById(1L);
    }

    @And("workload delete action should be triggered")
    public void workload_delete_action_should_be_triggered() {

        verify(workloadConnection, times(1))
                .updateWorkload(
                        any(TrainerWorkloadRequest.class),
                        eq("token"),
                        eq("transaction-id")
                );
    }

    @Given("training does not exist")
    public void training_does_not_exist() {

        when(trainingRepository.findById(1L))
                .thenReturn(Optional.empty());
    }

    @Then("training not found exception should be thrown")
    public void training_not_found_exception_should_be_thrown() {

        Assertions.assertNotNull(exception);

        Assertions.assertEquals(
                "Training not found",
                exception.getMessage()
        );
    }
}