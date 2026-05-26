package com.epam.gym.workload.component.steps;

import com.epam.gym.workload.config.security.JmsSecurityService;
import com.epam.gym.workload.dto.TrainerWorkloadRequest;
import com.epam.gym.workload.enums.ActionType;
import com.epam.gym.workload.message.WorkloadMessageListener;
import com.epam.gym.workload.service.WorkloadService;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

public class WorkloadMessageListenerStepDefinition {

    @Mock
    private WorkloadService workloadService;

    @Mock
    private JmsSecurityService jmsSecurityService;

    private WorkloadMessageListener listener;

    private TrainerWorkloadRequest request;

    private String token;

    private Exception exception;

    @Before
    public void setup() {

        MockitoAnnotations.openMocks(this);

        listener =
                new WorkloadMessageListener(
                        workloadService,
                        jmsSecurityService
                );
    }

    @Given("valid workload JMS message")
    public void valid_workload_jms_message() {

        request = new TrainerWorkloadRequest();

        request.setTrainerUsername("trainer");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setTrainingDate(LocalDate.now());
        request.setTrainingDuration(60);
        request.setActionType(ActionType.ADD);
        request.setStatus(true);
    }

    @Given("valid jwt token")
    public void valid_jwt_token() {

        token = "valid-token";
    }

    @Given("invalid jwt token")
    public void invalid_jwt_token() {

        token = "invalid-token";

        doThrow(new SecurityException("Invalid token"))
                .when(jmsSecurityService)
                .validateToken(token);
    }

    @Given("workload service failure")
    public void workload_service_failure() {

        doThrow(new RuntimeException("Database error"))
                .when(workloadService)
                .updateWorkload(any());
    }

    @When("workload message is consumed")
    public void workload_message_is_consumed() {

        try {

            listener.updateWorkload(
                    request,
                    token,
                    "tx-123"
            );

        } catch (Exception ex) {
            exception = ex;
        }
    }

    @Then("workload service should be invoked")
    public void workload_service_should_be_invoked() {

        verify(workloadService, times(1))
                .updateWorkload(request);
    }

    @Then("security exception should be thrown")
    public void security_exception_should_be_thrown() {

        Assertions.assertNotNull(exception);

        Assertions.assertTrue(
                exception.getCause() instanceof SecurityException
                        || exception instanceof SecurityException
        );
    }

    @Then("runtime exception should be thrown")
    public void runtime_exception_should_be_thrown() {

        Assertions.assertNotNull(exception);

        Assertions.assertTrue(
                exception.getCause() instanceof RuntimeException
                        || exception instanceof RuntimeException
        );
    }
}