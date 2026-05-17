package com.epam.gym.workload.service;

import com.epam.gym.workload.dto.MonthlySummaryDTOImpl;
import com.epam.gym.workload.dto.TrainerWorkloadRequest;
import com.epam.gym.workload.dto.TrainerWorkloadSummeryResponse;
import com.epam.gym.workload.entity.MonthSummary;
import com.epam.gym.workload.entity.Workload;
import com.epam.gym.workload.entity.YearSummary;
import com.epam.gym.workload.enums.ActionType;
import com.epam.gym.workload.mapper.WorkloadMapperI;
import com.epam.gym.workload.repository.WorkloadRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadServiceTest {

    @Mock
    private WorkloadRepository workloadRepository;

    @Mock
    private WorkloadMapperI workloadMapper;

    @InjectMocks
    private WorkloadService workloadService;

    @Test
    void updateWorkload_shouldAddNewMonthToExistingYear_whenActionIsAddAndYearExistsButMonthDoesNot() {
        // Arrange
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setTrainingDate(LocalDate.of(2026, 6, 15)); // Month 6
        request.setTrainingDuration(45);
        request.setActionType(ActionType.ADD);

        MonthSummary existingMonthMay = MonthSummary.builder()
                .month(5)
                .trainingSummaryDuration(30)
                .build();

        YearSummary existingYear = YearSummary.builder()
                .year(2026)
                .months(new ArrayList<>(List.of(existingMonthMay)))
                .build();

        Workload workload = new Workload();
        workload.setYears(new ArrayList<>(List.of(existingYear)));

        when(workloadRepository.findByTrainerUsername("john"))
                .thenReturn(Optional.of(workload));

        // Act
        workloadService.updateWorkload(request);

        // Assert
        assertEquals(1, workload.getYears().size());
        assertEquals(2, existingYear.getMonths().size());

        MonthSummary addedMonth = existingYear.getMonths().stream()
                .filter(m -> m.getMonth() == 6)
                .findFirst()
                .orElseThrow();

        assertEquals(45, addedMonth.getTrainingSummaryDuration());
        verify(workloadRepository).save(workload);
    }

    @Test
    void updateWorkload_shouldAddNewYear_whenActionIsAddAndTrainerExistsButYearDoesNotExist() {
        // Arrange
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setTrainingDate(LocalDate.of(2026, 5, 10)); // Year 2026
        request.setTrainingDuration(20);
        request.setActionType(ActionType.ADD);

        YearSummary existingYear2025 = YearSummary.builder()
                .year(2025)
                .months(new ArrayList<>())
                .build();

        Workload workload = new Workload();
        workload.setYears(new ArrayList<>(List.of(existingYear2025)));

        when(workloadRepository.findByTrainerUsername("john"))
                .thenReturn(Optional.of(workload));

        // Act
        workloadService.updateWorkload(request);

        // Assert
        assertEquals(2, workload.getYears().size());
        assertTrue(workload.getYears().stream().anyMatch(y -> y.getYear() == 2026));
        verify(workloadRepository).save(workload);
    }

    @Test
    void updateWorkload_shouldRemoveMonthButKeepYear_whenDeleteEmptiesMonthButYearHasOtherMonths() {
        // Arrange
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setTrainingDate(LocalDate.of(2026, 5, 10));
        request.setTrainingDuration(20); // Will wipe out May
        request.setActionType(ActionType.DELETE);

        MonthSummary may = MonthSummary.builder()
                .month(5)
                .trainingSummaryDuration(20)
                .build();

        MonthSummary june = MonthSummary.builder()
                .month(6)
                .trainingSummaryDuration(40)
                .build();

        YearSummary yearSummary = YearSummary.builder()
                .year(2026)
                .months(new ArrayList<>(List.of(may, june)))
                .build();

        Workload workload = new Workload();
        workload.setYears(new ArrayList<>(List.of(yearSummary)));

        when(workloadRepository.findByTrainerUsername("john"))
                .thenReturn(Optional.of(workload));

        // Act
        workloadService.updateWorkload(request);

        // Assert
        assertEquals(1, workload.getYears().size()); // Year is kept
        assertEquals(1, yearSummary.getMonths().size()); // Only June remains
        assertEquals(6, yearSummary.getMonths().get(0).getMonth());
        verify(workloadRepository).save(workload);
    }

    @Test
    void getWorkload_shouldReturnEmptyYearsList_whenDataExistsButNoRowsMatchCriteria() {
        // Arrange
        MonthlySummaryDTOImpl mismatchDto = new MonthlySummaryDTOImpl();
        mismatchDto.setTrainerUsername("john");
        mismatchDto.setFirstName("John");
        mismatchDto.setLastName("Doe");
        mismatchDto.setYear(2025);
        mismatchDto.setMonth(12);
        mismatchDto.setTotalDuration(100);

        when(workloadRepository.getMonthlySummary("john"))
                .thenReturn(List.of(mismatchDto));

        // Act
        TrainerWorkloadSummeryResponse response = workloadService.getWorkload("john", 2026, 5);

        // Assert
        assertNotNull(response);
        assertNull(response.getUsername()); // Fields are not set because block condition rowYear == year failed
        assertTrue(response.getYears().isEmpty());
    }

    @Test
    void updateWorkload_shouldCreateNewWorkload_whenNotExists() {

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setTrainingDate(LocalDate.of(2026, 5, 10));
        request.setTrainingDuration(20);
        request.setActionType(ActionType.ADD);

        Workload workload = new Workload();
        workload.setYears(new ArrayList<>());

        when(workloadRepository.findByTrainerUsername("john"))
                .thenReturn(Optional.empty());

        when(workloadMapper.toEntity(request))
                .thenReturn(workload);

        workloadService.updateWorkload(request);

        verify(workloadRepository).save(workload);
    }

    @Test
    void updateWorkload_shouldDecreaseDuration_whenDeleteAndDurationRemainsPositive() {

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setTrainingDate(LocalDate.of(2026, 5, 10));
        request.setTrainingDuration(10);
        request.setActionType(ActionType.DELETE);

        MonthSummary month = MonthSummary.builder()
                .month(5)
                .trainingSummaryDuration(40)
                .build();

        YearSummary year = YearSummary.builder()
                .year(2026)
                .months(new ArrayList<>(List.of(month)))
                .build();

        Workload workload = new Workload();
        workload.setYears(new ArrayList<>(List.of(year)));

        when(workloadRepository.findByTrainerUsername("john"))
                .thenReturn(Optional.of(workload));

        workloadService.updateWorkload(request);

        assertEquals(30, month.getTrainingSummaryDuration());

        verify(workloadRepository).save(workload);
    }

    @Test
    void updateWorkload_shouldRemoveMonthOnly_whenOtherMonthsExist() {

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setTrainingDate(LocalDate.of(2026, 5, 10));
        request.setTrainingDuration(30);
        request.setActionType(ActionType.DELETE);

        MonthSummary may = MonthSummary.builder()
                .month(5)
                .trainingSummaryDuration(30)
                .build();

        MonthSummary june = MonthSummary.builder()
                .month(6)
                .trainingSummaryDuration(50)
                .build();

        YearSummary year = YearSummary.builder()
                .year(2026)
                .months(new ArrayList<>(List.of(may, june)))
                .build();

        Workload workload = new Workload();
        workload.setYears(new ArrayList<>(List.of(year)));

        when(workloadRepository.findByTrainerUsername("john"))
                .thenReturn(Optional.of(workload));

        workloadService.updateWorkload(request);

        assertEquals(1, year.getMonths().size());
        assertEquals(6, year.getMonths().getFirst().getMonth());

        verify(workloadRepository).save(workload);
    }

    @Test
    void updateWorkload_shouldThrowException_whenRequestIsNull() {

        NullPointerException ex =
                assertThrows(
                        NullPointerException.class,
                        () -> workloadService.updateWorkload(null)
                );

        assertEquals("Request cannot be null", ex.getMessage());
    }

    @Test
    void updateWorkload_shouldThrowException_whenDeleteAndWorkloadNotFound() {

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setTrainingDate(LocalDate.now());
        request.setTrainingDuration(10);
        request.setActionType(ActionType.DELETE);

        when(workloadRepository.findByTrainerUsername("john"))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> workloadService.updateWorkload(request)
        );

        assertEquals("Workload not found", ex.getMessage());

        verify(workloadRepository, never()).save(any());
    }

    @Test
    void updateWorkload_shouldThrowException_whenDeleteAndYearNotFound() {

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setTrainingDate(LocalDate.of(2026, 5, 10));
        request.setTrainingDuration(10);
        request.setActionType(ActionType.DELETE);

        YearSummary differentYear = YearSummary.builder()
                .year(2025)
                .months(new ArrayList<>())
                .build();

        Workload workload = new Workload();
        workload.setYears(new ArrayList<>(List.of(differentYear)));

        when(workloadRepository.findByTrainerUsername("john"))
                .thenReturn(Optional.of(workload));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> workloadService.updateWorkload(request)
        );

        assertEquals("Year not found", ex.getMessage());

        verify(workloadRepository, never()).save(any());
    }

    @Test
    void updateWorkload_shouldThrowException_whenDeleteAndMonthNotFound() {

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setTrainingDate(LocalDate.of(2026, 5, 10));
        request.setTrainingDuration(10);
        request.setActionType(ActionType.DELETE);

        MonthSummary differentMonth = MonthSummary.builder()
                .month(4)
                .trainingSummaryDuration(20)
                .build();

        YearSummary yearSummary = YearSummary.builder()
                .year(2026)
                .months(new ArrayList<>(List.of(differentMonth)))
                .build();

        Workload workload = new Workload();
        workload.setYears(new ArrayList<>(List.of(yearSummary)));

        when(workloadRepository.findByTrainerUsername("john"))
                .thenReturn(Optional.of(workload));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> workloadService.updateWorkload(request)
        );

        assertEquals("Month not found", ex.getMessage());

        verify(workloadRepository, never()).save(any());
    }

    @Test
    void updateWorkload_shouldInitializeYears_whenYearsIsNull() {

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setTrainingDate(LocalDate.of(2026, 5, 10));
        request.setTrainingDuration(20);
        request.setActionType(ActionType.ADD);

        Workload workload = new Workload();
        workload.setYears(null);

        when(workloadRepository.findByTrainerUsername("john"))
                .thenReturn(Optional.of(workload));

        workloadService.updateWorkload(request);

        assertNotNull(workload.getYears());
        assertEquals(1, workload.getYears().size());

        verify(workloadRepository).save(workload);
    }

    @Test
    void updateWorkload_shouldInitializeMonths_whenMonthsIsNull() {

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setTrainingDate(LocalDate.of(2026, 5, 10));
        request.setTrainingDuration(20);
        request.setActionType(ActionType.ADD);

        YearSummary yearSummary = YearSummary.builder()
                .year(2026)
                .months(null)
                .build();

        Workload workload = new Workload();
        workload.setYears(new ArrayList<>(List.of(yearSummary)));

        when(workloadRepository.findByTrainerUsername("john"))
                .thenReturn(Optional.of(workload));

        workloadService.updateWorkload(request);

        assertNotNull(yearSummary.getMonths());
        assertEquals(1, yearSummary.getMonths().size());

        verify(workloadRepository).save(workload);
    }

    @Test
    void updateWorkload_shouldIncreaseDuration_whenMonthAlreadyExists() {

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setTrainingDate(LocalDate.of(2026, 5, 10));
        request.setTrainingDuration(15);
        request.setActionType(ActionType.ADD);

        MonthSummary monthSummary = MonthSummary.builder()
                .month(5)
                .trainingSummaryDuration(25)
                .build();

        YearSummary yearSummary = YearSummary.builder()
                .year(2026)
                .months(new ArrayList<>(List.of(monthSummary)))
                .build();

        Workload workload = new Workload();
        workload.setYears(new ArrayList<>(List.of(yearSummary)));

        when(workloadRepository.findByTrainerUsername("john"))
                .thenReturn(Optional.of(workload));

        workloadService.updateWorkload(request);

        assertEquals(
                40,
                monthSummary.getTrainingSummaryDuration()
        );

        verify(workloadRepository).save(workload);
    }

    @Test
    void updateWorkload_shouldRemoveMonth_whenDeleteMakesDurationNegative() {

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setTrainingDate(LocalDate.of(2026, 5, 10));
        request.setTrainingDuration(50);
        request.setActionType(ActionType.DELETE);

        MonthSummary monthSummary = MonthSummary.builder()
                .month(5)
                .trainingSummaryDuration(20)
                .build();

        YearSummary yearSummary = YearSummary.builder()
                .year(2026)
                .months(new ArrayList<>(List.of(monthSummary)))
                .build();

        Workload workload = new Workload();
        workload.setYears(new ArrayList<>(List.of(yearSummary)));

        when(workloadRepository.findByTrainerUsername("john"))
                .thenReturn(Optional.of(workload));

        workloadService.updateWorkload(request);

        assertTrue(workload.getYears().isEmpty());

        verify(workloadRepository).save(workload);
    }

    @Test
    void getWorkload_shouldReturnMultipleMatchingRows() {

        MonthlySummaryDTOImpl dto1 = new MonthlySummaryDTOImpl();
        dto1.setTrainerUsername("john");
        dto1.setFirstName("John");
        dto1.setLastName("Doe");
        dto1.setYear(2024);
        dto1.setMonth(6);
        dto1.setTotalDuration(10);

        MonthlySummaryDTOImpl dto2 = new MonthlySummaryDTOImpl();
        dto2.setTrainerUsername("john");
        dto2.setFirstName("John");
        dto2.setLastName("Doe");
        dto2.setYear(2024);
        dto2.setMonth(6);
        dto2.setTotalDuration(20);

        when(workloadRepository.getMonthlySummary("john"))
                .thenReturn(List.of(dto1, dto2));

        TrainerWorkloadSummeryResponse response =
                workloadService.getWorkload("john", 2024, 6);

        assertEquals(2, response.getYears().size());
    }

    @Test
    void getWorkload_shouldFilterOnlyMatchingRows() {

        MonthlySummaryDTOImpl matching = new MonthlySummaryDTOImpl();
        matching.setTrainerUsername("john");
        matching.setFirstName("John");
        matching.setLastName("Doe");
        matching.setYear(2024);
        matching.setMonth(6);
        matching.setTotalDuration(10);

        MonthlySummaryDTOImpl nonMatching = new MonthlySummaryDTOImpl();
        nonMatching.setTrainerUsername("john");
        nonMatching.setFirstName("John");
        nonMatching.setLastName("Doe");
        nonMatching.setYear(2025);
        nonMatching.setMonth(7);
        nonMatching.setTotalDuration(50);

        when(workloadRepository.getMonthlySummary("john"))
                .thenReturn(List.of(matching, nonMatching));

        TrainerWorkloadSummeryResponse response =
                workloadService.getWorkload("john", 2024, 6);

        assertEquals(1, response.getYears().size());

        assertEquals(
                10,
                response.getYears().getFirst().getDuration()
        );
    }

    @Test
    void updateWorkload_shouldSaveWorkload_whenActionIsAdd() {

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setTrainingDate(LocalDate.now());
        request.setTrainingDuration(5);
        request.setActionType(ActionType.ADD);

        Workload workload = new Workload();
        workload.setYears(new ArrayList<>());

        when(workloadRepository.findByTrainerUsername("john"))
                .thenReturn(Optional.empty());

        when(workloadMapper.toEntity(request))
                .thenReturn(workload);

        workloadService.updateWorkload(request);

        verify(workloadRepository).save(workload);
    }

    @Test
    void updateWorkload_shouldUpdateExistingWorkload_whenActionIsDelete() {

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainerUsername("john");
        request.setTrainingDuration(10);
        request.setTrainingDate(LocalDate.now());
        request.setActionType(ActionType.DELETE);

        MonthSummary monthSummary = MonthSummary.builder()
                .month(request.getTrainingDate().getMonthValue())
                .trainingSummaryDuration(20)
                .build();

        YearSummary yearSummary = YearSummary.builder()
                .year(request.getTrainingDate().getYear())
                .months(new ArrayList<>(List.of(monthSummary)))
                .build();

        Workload workload = new Workload();
        workload.setTrainerUsername("john");
        workload.setYears(new ArrayList<>(List.of(yearSummary)));

        when(workloadRepository.findByTrainerUsername("john"))
                .thenReturn(Optional.of(workload));

        workloadService.updateWorkload(request);

        assertEquals(
                10,
                workload.getYears()
                        .getFirst()
                        .getMonths()
                        .getFirst()
                        .getTrainingSummaryDuration()
        );

        verify(workloadRepository).save(workload);
        verify(workloadRepository, never()).delete(workload);
    }


    @Test
    void getWorkload_shouldReturnNull_whenNoData() {

        when(workloadRepository.getMonthlySummary("john"))
                .thenReturn(List.of());

        TrainerWorkloadSummeryResponse response =
                workloadService.getWorkload("john", 2024, 6);

        assertNull(response);
    }

    @Test
    void getWorkload_shouldReturnFilteredData() {

        MonthlySummaryDTOImpl dto = new MonthlySummaryDTOImpl();

        dto.setTrainerUsername("john");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setYear(2024);
        dto.setMonth(6);
        dto.setTotalDuration(10);

        when(workloadRepository.getMonthlySummary("john"))
                .thenReturn(List.of(dto));

        TrainerWorkloadSummeryResponse response =
                workloadService.getWorkload("john", 2024, 6);

        assertNotNull(response);

        assertEquals("john", response.getUsername());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());

        assertEquals(1, response.getYears().size());

        assertEquals(2024,
                response.getYears().getFirst().getYear());

        assertEquals(6,
                response.getYears().getFirst().getMonth());

        assertEquals(10,
                response.getYears().getFirst().getDuration());
    }

    @Test
    void getWorkload_shouldIgnoreDifferentMonthOrYear() {

        MonthlySummaryDTOImpl dto = new MonthlySummaryDTOImpl();

        dto.setTrainerUsername("john");
        dto.setFirstName("John");
        dto.setLastName("Doe");

        dto.setYear(2023);
        dto.setMonth(5);

        dto.setTotalDuration(10);

        when(workloadRepository.getMonthlySummary("john"))
                .thenReturn(List.of(dto));

        TrainerWorkloadSummeryResponse response =
                workloadService.getWorkload("john", 2024, 6);

        assertNotNull(response);

        assertTrue(response.getYears().isEmpty());
    }

    @Test
    void getWorkload_shouldHandleNullNames() {

        MonthlySummaryDTOImpl dto = new MonthlySummaryDTOImpl();

        dto.setTrainerUsername("john");
        dto.setFirstName(null);
        dto.setLastName(null);
        dto.setYear(2024);
        dto.setMonth(6);
        dto.setTotalDuration(10);

        when(workloadRepository.getMonthlySummary("john"))
                .thenReturn(List.of(dto));

        TrainerWorkloadSummeryResponse response =
                workloadService.getWorkload("john", 2024, 6);

        assertNotNull(response);

        assertNull(response.getFirstName());
        assertNull(response.getLastName());
    }

    @Test
    void getWorkload_shouldReturnEmptyYears_whenNoMatchButDataExists() {

        MonthlySummaryDTOImpl dto = new MonthlySummaryDTOImpl();

        dto.setTrainerUsername("john");
        dto.setFirstName(null);
        dto.setLastName(null);

        dto.setYear(2025);
        dto.setMonth(7);

        dto.setTotalDuration(10);

        when(workloadRepository.getMonthlySummary("john"))
                .thenReturn(List.of(dto));

        TrainerWorkloadSummeryResponse response =
                workloadService.getWorkload("john", 2024, 6);

        assertNotNull(response);

        assertTrue(response.getYears().isEmpty());
    }

    @Nested
    @DisplayName("Make these tests pass without changing them")
    class ShouldPass {

        private static final String TRAINER_USERNAME = "john";
        private static final LocalDate TRAINING_DATE = LocalDate.of(2026, 5, 10);

        @Test
        void updateWorkload_shouldNotDoubleCountDuration_whenAddCreatesNewMonth() {

            final var request = buildRequest(ActionType.ADD, 60);

            final var existingYear = YearSummary.builder()
                    .year(2026)
                    .months(new ArrayList<>())
                    .build();

            final var workload = new Workload();
            workload.setYears(new ArrayList<>(List.of(existingYear)));

            when(workloadRepository.findByTrainerUsername(TRAINER_USERNAME))
                    .thenReturn(Optional.of(workload));

            workloadService.updateWorkload(request);

            assertEquals(1, workload.getYears().size());
            assertEquals(1, workload.getYears().getFirst().getMonths().size());
            assertEquals(
                    60,
                    workload.getYears().getFirst().getMonths().getFirst().getTrainingSummaryDuration(),
                    "Expected first insert to store duration once, without double counting"
            );

            verify(workloadRepository).save(workload);
        }

        @Test
        void updateWorkload_shouldDecrementExistingMonthDurationAndSave_whenActionIsDelete() {

            final var request = buildRequest(ActionType.DELETE, 30);
            final var workload = buildWorkload(90);

            when(workloadRepository.findByTrainerUsername(TRAINER_USERNAME))
                    .thenReturn(Optional.of(workload));

            workloadService.updateWorkload(request);

            assertEquals(
                    60,
                    workload.getYears().getFirst().getMonths().getFirst().getTrainingSummaryDuration(),
                    "Expected DELETE to decrement summary duration for the target month"
            );

            verify(workloadRepository).save(workload);
//            verify(workloadRepository, never()).deleteWorkloads(any(), anyInt());
        }

        @Test
        void updateWorkload_shouldRemoveEmptyMonthAndYearButKeepTrainerDocument_whenDeleteReachesZero() {

            final var request = buildRequest(ActionType.DELETE, 30);
            final var workload = buildWorkload(30);
            workload.setFirstName("John");
            workload.setLastName("Doe");
            workload.setStatus(true);

            when(workloadRepository.findByTrainerUsername(TRAINER_USERNAME))
                    .thenReturn(Optional.of(workload));

            workloadService.updateWorkload(request);

            assertEquals(TRAINER_USERNAME, workload.getTrainerUsername());
            assertEquals("John", workload.getFirstName());
            assertEquals("Doe", workload.getLastName());
            assertTrue(workload.getStatus());
            assertTrue(
                    workload.getYears().isEmpty(),
                    "Expected year to be removed when its last month reaches zero duration"
            );

            verify(workloadRepository).save(workload);
//            verify(workloadRepository, never()).deleteWorkloads(any(), anyInt());
        }

        private TrainerWorkloadRequest buildRequest(final ActionType actionType, final int duration) {
            final var request = new TrainerWorkloadRequest();
            request.setTrainerUsername(TRAINER_USERNAME);
            request.setTrainingDate(TRAINING_DATE);
            request.setTrainingDuration(duration);
            request.setActionType(actionType);
            return request;
        }

        private Workload buildWorkload(final int monthDuration) {
            final var monthSummary = MonthSummary.builder()
                    .month(5)
                    .trainingSummaryDuration(monthDuration)
                    .build();

            final var yearSummary = YearSummary.builder()
                    .year(2026)
                    .months(new ArrayList<>(List.of(monthSummary)))
                    .build();

            final var workload = new Workload();
            workload.setTrainerUsername(TRAINER_USERNAME);
            workload.setYears(new ArrayList<>(List.of(yearSummary)));
            return workload;
        }
    }
}