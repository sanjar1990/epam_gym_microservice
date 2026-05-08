package com.epam.gym.workload.service;

import com.epam.gym.workload.dto.TrainerWorkloadRequest;
import com.epam.gym.workload.dto.TrainerWorkloadSummeryResponse;
import com.epam.gym.workload.entity.Workload;
import com.epam.gym.workload.enums.ActionType;
import com.epam.gym.workload.mapper.WorkloadMapperI;
import com.epam.gym.workload.repository.WorkloadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// TODO:
//  Compilation error! --fixed
@ExtendWith(MockitoExtension.class)
class WorkloadServiceTest {

    @Mock
    private WorkloadRepository workloadRepository;

    @Mock
    private WorkloadMapperI workloadMapper;

    @InjectMocks
    private WorkloadService workloadService;

    @Test
    void calculateWorkingHours_shouldSavePositiveDuration_whenActionIsAdd() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainingDuration(5);
        request.setActionType(ActionType.ADD);

        Workload workload = new Workload();
        workload.setTrainingDuration(5);

        when(workloadMapper.toEntity(request)).thenReturn(workload);

        workloadService.updateWorkload(request);

        verify(workloadRepository).save(workload);
        assertEquals(5, workload.getTrainingDuration());
    }
    @Test
    void updateWorkload_shouldNotCallDelete_whenActionIsAdd() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setActionType(ActionType.ADD);

        when(workloadMapper.toEntity(request)).thenReturn(new Workload());

        workloadService.updateWorkload(request);

        verify(workloadRepository, never())
                .deleteWorkloads(any(LocalDate.class), anyInt());
    }
    @Test
    void updateWorkload_shouldNotCallSave_whenActionIsDelete() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setActionType(ActionType.DELETE);
        request.setTrainingDate(LocalDate.now());
        request.setTrainingDuration(10);

        workloadService.updateWorkload(request);

        verify(workloadRepository, never()).save(any());
    }
    @Test
    void updateWorkload_shouldHandleNullDate_whenDelete() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setActionType(ActionType.DELETE);
        request.setTrainingDuration(10);
        request.setTrainingDate(null);

        workloadService.updateWorkload(request);

        verify(workloadRepository)
                .deleteWorkloads(null, 10);
    }
    @Test
    void updateWorkload_shouldCallMapper_whenActionIsAdd() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setActionType(ActionType.ADD);

        workloadService.updateWorkload(request);

        verify(workloadMapper).toEntity(request);
    }
    @Test
    void updateWorkload_shouldCallDelete_whenActionIsDelete() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainingDuration(5);
        request.setTrainingDate(LocalDate.now().plusMonths(1)); // important!
        request.setActionType(ActionType.DELETE);

        workloadService.updateWorkload(request);

        verify(workloadRepository)
                .deleteWorkloads(request.getTrainingDate(), request.getTrainingDuration());
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
        Object[] row = new Object[]{
                "john",        // username
                "John",        // firstName
                "Doe",         // lastName
                true,          // isActive
                2024,          // year
                6,             // month
                10L            // duration
        };

        when(workloadRepository.getMonthlySummary("john"))
                .thenReturn(List.<Object[]>of(row));

        TrainerWorkloadSummeryResponse response =
                workloadService.getWorkload("john", 2024, 6);

        assertNotNull(response);
        assertEquals("john", response.getUsername());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertTrue(response.getIsActive());

        assertEquals(1, response.getYears().size());
        assertEquals(10, response.getYears().getFirst().getDuration());
        assertEquals(2024, response.getYears().getFirst().getYear());
        assertEquals(6, response.getYears().getFirst().getMonth());
    }

    @Test
    void getWorkload_shouldIgnoreDifferentMonthOrYear() {
        Object[] row = new Object[]{
                "john", "John", "Doe", true,
                2023, 5, 10L
        };

        when(workloadRepository.getMonthlySummary("john"))
                .thenReturn(List.<Object[]>of(row));

        TrainerWorkloadSummeryResponse response =
                workloadService.getWorkload("john", 2024, 6);

        assertNotNull(response);
        assertTrue(response.getYears().isEmpty());
    }

    @Test
    void updateWorkload_shouldCallSaveEvenIfMapperReturnsNull() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainingDuration(10);
        request.setActionType(ActionType.ADD);

        when(workloadMapper.toEntity(request)).thenReturn(null);

        workloadService.updateWorkload(request);

        verify(workloadRepository).save(null);
    }
    @Test
    void updateWorkload_shouldThrowException_whenRequestIsNull() {
        assertThrows(NullPointerException.class, () ->
                workloadService.updateWorkload(null)
        );
    }
    @Test
    void updateWorkload_shouldCallDelete_whenActionTypeIsNull() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainingDuration(10);
        request.setTrainingDate(LocalDate.now());

        workloadService.updateWorkload(request);

        verify(workloadRepository)
                .deleteWorkloads(request.getTrainingDate(), request.getTrainingDuration());
    }

    @Test
    void getWorkload_shouldStillSetUsernameFromRow() {
        Object[] row = new Object[]{
                "differentUser", "John", "Doe", true,
                2024, 6, 10L
        };
        when(workloadRepository.getMonthlySummary("john"))
                .thenReturn(List.<Object[]>of(row));

        TrainerWorkloadSummeryResponse response =
                workloadService.getWorkload("john", 2024, 6);

        assertNotNull(response);
        assertEquals("differentUser", response.getUsername());
    }
    @Test
    void getWorkload_shouldHandleNullNames() {
        Object[] row = new Object[]{
                "john", null, null, true,
                2024, 6, 10L
        };

        when(workloadRepository.getMonthlySummary("john"))
                .thenReturn(List.<Object[]>of(row));

        TrainerWorkloadSummeryResponse response =
                workloadService.getWorkload("john", 2024, 6);

        assertNotNull(response);
        assertNull(response.getFirstName());
        assertNull(response.getLastName());
    }
    @Test
    void getWorkload_shouldReturnEmptyYears_whenNoMatchButDataExists() {
        Object[] row = new Object[]{"john", "John", "Doe", true, 2023, 5, 10L};

        when(workloadRepository.getMonthlySummary("john"))
                .thenReturn(List.<Object[]>of(row));

        TrainerWorkloadSummeryResponse response =
                workloadService.getWorkload("john", 2024, 6);

        assertNotNull(response);
        assertTrue(response.getYears().isEmpty());
    }
}