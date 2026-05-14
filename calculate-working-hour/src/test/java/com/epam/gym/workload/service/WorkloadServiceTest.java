package com.epam.gym.workload.service;

import com.epam.gym.workload.dto.MonthlySummaryDTOImpl;
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
    void updateWorkload_shouldCallDelete_whenActionIsDelete() {

        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setTrainingDuration(10);
        request.setTrainingDate(LocalDate.now());
        request.setActionType(ActionType.DELETE);

        workloadService.updateWorkload(request);

        verify(workloadRepository)
                .deleteWorkloads(request.getTrainingDate(), 10);

        verify(workloadRepository, never()).save(any());
    }

    @Test
    void updateWorkload_shouldThrowException_whenRequestIsNull() {

        assertThrows(
                NullPointerException.class,
                () -> workloadService.updateWorkload(null)
        );
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
}