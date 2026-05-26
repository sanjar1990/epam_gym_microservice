package com.epam.gym.workload.component.steps;

import com.epam.gym.workload.dto.MonthlySummaryDTO;
import com.epam.gym.workload.dto.TrainerWorkloadRequest;
import com.epam.gym.workload.dto.TrainerWorkloadSummeryResponse;
import com.epam.gym.workload.entity.MonthSummary;
import com.epam.gym.workload.entity.Workload;
import com.epam.gym.workload.entity.YearSummary;
import com.epam.gym.workload.enums.ActionType;
import com.epam.gym.workload.mapper.WorkloadMapperI;
import com.epam.gym.workload.repository.WorkloadRepository;
import com.epam.gym.workload.service.WorkloadService;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class WorkloadServiceStepDefinition {

    @Mock
    private WorkloadRepository workloadRepository;

    @Mock
    private WorkloadMapperI workloadMapperI;

    @Mock
    private MonthlySummaryDTO monthlySummaryDTO;

    private WorkloadService workloadService;

    private TrainerWorkloadRequest request;

    private Workload workload;

    private Exception exception;

    private TrainerWorkloadSummeryResponse response;

    @Before
    public void setup() {

        MockitoAnnotations.openMocks(this);

        workloadService =
                new WorkloadService(
                        workloadRepository,
                        workloadMapperI
                );

        MDC.put("transactionId", "tx-123");
    }

    @Given("valid add workload request")
    public void valid_add_workload_request() {

        request = buildRequest(ActionType.ADD, 60);
    }

    @Given("workload does not exist")
    public void workload_does_not_exist() {

        workload = new Workload();
        workload.setYears(new ArrayList<>());

        when(workloadRepository.findByTrainerUsername("trainer"))
                .thenReturn(Optional.empty());

        when(workloadMapperI.toEntity(request))
                .thenReturn(workload);
    }

    @When("workload update is performed")
    public void workload_update_is_performed() {

        try {
            workloadService.updateWorkload(request);
        } catch (Exception ex) {
            exception = ex;
        }
    }

    @Then("workload should be saved")
    public void workload_should_be_saved() {

        verify(workloadRepository, times(1))
                .save(any(Workload.class));
    }

    @Then("workload duration should be increased")
    public void workload_duration_should_be_increased() {

        MonthSummary month =
                workload.getYears()
                        .getFirst()
                        .getMonths()
                        .getFirst();

        int expected =
                request.getActionType() == ActionType.ADD
                        && request.getTrainingDuration() == 60
                        ? 60
                        : 90;

        Assertions.assertEquals(
                expected,
                month.getTrainingSummaryDuration()
        );
    }

    @Given("existing workload with month summary")
    public void existing_workload_with_month_summary() {

        request = buildRequest(ActionType.ADD, 30);

        MonthSummary month =
                MonthSummary.builder()
                        .month(5)
                        .trainingSummaryDuration(60)
                        .build();

        YearSummary year =
                YearSummary.builder()
                        .year(2025)
                        .months(new ArrayList<>(List.of(month)))
                        .build();

        workload = new Workload();
        workload.setYears(new ArrayList<>(List.of(year)));

        when(workloadRepository.findByTrainerUsername("trainer"))
                .thenReturn(Optional.of(workload));
    }

    @Given("existing workload for delete")
    public void existing_workload_for_delete() {

        request = buildRequest(ActionType.DELETE, 30);

        MonthSummary month =
                MonthSummary.builder()
                        .month(5)
                        .trainingSummaryDuration(90)
                        .build();

        YearSummary year =
                YearSummary.builder()
                        .year(2025)
                        .months(new ArrayList<>(List.of(month)))
                        .build();

        workload = new Workload();
        workload.setYears(new ArrayList<>(List.of(year)));

        when(workloadRepository.findByTrainerUsername("trainer"))
                .thenReturn(Optional.of(workload));
    }

    @When("delete workload update is performed")
    public void delete_workload_update_is_performed() {

        try {
            workloadService.updateWorkload(request);
        } catch (Exception ex) {
            exception = ex;
        }
    }

    @Then("workload duration should be decreased")
    public void workload_duration_should_be_decreased() {

        MonthSummary month =
                workload.getYears()
                        .getFirst()
                        .getMonths()
                        .getFirst();

        Assertions.assertEquals(
                60,
                month.getTrainingSummaryDuration()
        );
    }

    @Given("existing workload with exact duration")
    public void existing_workload_with_exact_duration() {

        request = buildRequest(ActionType.DELETE, 60);

        MonthSummary month =
                MonthSummary.builder()
                        .month(5)
                        .trainingSummaryDuration(60)
                        .build();

        YearSummary year =
                YearSummary.builder()
                        .year(2025)
                        .months(new ArrayList<>(List.of(month)))
                        .build();

        workload = new Workload();
        workload.setYears(new ArrayList<>(List.of(year)));

        when(workloadRepository.findByTrainerUsername("trainer"))
                .thenReturn(Optional.of(workload));
    }

    @Then("month summary should be removed")
    public void month_summary_should_be_removed() {

        Assertions.assertTrue(
                workload.getYears().isEmpty()
                        || workload.getYears()
                        .getFirst()
                        .getMonths()
                        .isEmpty()
        );
    }

    @Given("existing workload with single month")
    public void existing_workload_with_single_month() {

        existing_workload_with_exact_duration();
    }

    @Then("year summary should be removed")
    public void year_summary_should_be_removed() {

        Assertions.assertTrue(
                workload.getYears().isEmpty()
        );
    }

    @Given("null workload request")
    public void null_workload_request() {

        request = null;
    }

    @Then("null request exception should be thrown")
    public void null_request_exception_should_be_thrown() {

        Assertions.assertNotNull(exception);

        Assertions.assertEquals(
                "Request cannot be null",
                exception.getMessage()
        );
    }

    @Given("delete request with missing workload")
    public void delete_request_with_missing_workload() {

        request = buildRequest(ActionType.DELETE, 30);

        when(workloadRepository.findByTrainerUsername("trainer"))
                .thenReturn(Optional.empty());
    }

    @Then("workload not found exception should be thrown")
    public void workload_not_found_exception_should_be_thrown() {

        Assertions.assertNotNull(exception);

        Assertions.assertEquals(
                "Workload not found",
                exception.getMessage()
        );
    }

    @Given("monthly summary exists")
    public void monthly_summary_exists() {

        when(monthlySummaryDTO.getTrainerUsername())
                .thenReturn("trainer");

        when(monthlySummaryDTO.getFirstName())
                .thenReturn("John");

        when(monthlySummaryDTO.getLastName())
                .thenReturn("Doe");

        when(monthlySummaryDTO.getYear())
                .thenReturn(2025);

        when(monthlySummaryDTO.getMonth())
                .thenReturn(5);

        when(monthlySummaryDTO.getTotalDuration())
                .thenReturn(120);

        when(workloadRepository.getMonthlySummary("trainer"))
                .thenReturn(List.of(monthlySummaryDTO));
    }

    @When("workload summary is requested")
    public void workload_summary_is_requested() {

        response =
                workloadService.getWorkload(
                        "trainer",
                        2025,
                        5
                );
    }

    @Then("workload summary response should be returned")
    public void workload_summary_response_should_be_returned() {

        Assertions.assertNotNull(response);

        Assertions.assertEquals(
                "trainer",
                response.getUsername()
        );

        Assertions.assertEquals(
                1,
                response.getYears().size()
        );
    }

    @Given("no workload summary exists")
    public void no_workload_summary_exists() {

        when(workloadRepository.getMonthlySummary("trainer"))
                .thenReturn(List.of());
    }

    @Then("workload summary response should be null")
    public void workload_summary_response_should_be_null() {

        Assertions.assertNull(response);
    }

    private TrainerWorkloadRequest buildRequest(
            ActionType actionType,
            int duration
    ) {

        TrainerWorkloadRequest request =
                new TrainerWorkloadRequest();

        request.setTrainerUsername("trainer");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setTrainingDate(LocalDate.of(2025, 5, 10));
        request.setTrainingDuration(duration);
        request.setActionType(actionType);
        request.setStatus(true);

        return request;
    }
}