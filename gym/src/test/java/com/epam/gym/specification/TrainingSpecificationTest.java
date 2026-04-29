package com.epam.gym.specification;

import com.epam.gym.dto.GetTraineeTrainingsCriteriaFilterDTO;
import com.epam.gym.dto.GetTrainerTrainingsCriteriaFilterDTO;
import com.epam.gym.entity.Training;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class TrainingSpecificationTest {

    @Test
    void filterByCriteriaForTrainee_shouldApplyAllFilters() {
        GetTraineeTrainingsCriteriaFilterDTO dto = new GetTraineeTrainingsCriteriaFilterDTO();
        dto.setTraineeUsername("john");
        dto.setTrainerName("trainer1");
        dto.setTrainingType("YOGA");
        dto.setFromDate(LocalDate.now().minusDays(5));
        dto.setToDate(LocalDate.now());

        Root<Training> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate predicate = mock(Predicate.class);
        Predicate finalPredicate = mock(Predicate.class);

        Join traineeJoin = mock(Join.class);
        Join traineeUserJoin = mock(Join.class);
        Join trainerJoin = mock(Join.class);
        Join trainerUserJoin = mock(Join.class);
        Join typeJoin = mock(Join.class);

        Path usernamePath = mock(Path.class);
        Path trainingDatePath = mock(Path.class);
        Path typeNamePath = mock(Path.class);

        when(cb.conjunction()).thenReturn(predicate);
        when(cb.and(any(), any())).thenReturn(finalPredicate);

        when(root.join("trainee")).thenReturn(traineeJoin);
        when(traineeJoin.join("user")).thenReturn(traineeUserJoin);
        when(traineeUserJoin.get("username")).thenReturn(usernamePath);

        when(root.get("trainingDate")).thenReturn(trainingDatePath);

        when(root.join("trainingType")).thenReturn(typeJoin);
        when(typeJoin.get("trainingTypeName")).thenReturn(typeNamePath);

        when(root.join("trainer")).thenReturn(trainerJoin);
        when(trainerJoin.join("user")).thenReturn(trainerUserJoin);
        when(trainerUserJoin.get("username")).thenReturn(usernamePath);

        when(cb.equal(any(), any())).thenReturn(predicate);
        when(cb.like(any(), anyString())).thenReturn(predicate);
        when(cb.lower(any())).thenReturn(usernamePath);
        when(cb.and(any(Predicate.class), any(Predicate.class)))
                .thenReturn(finalPredicate);
        when(cb.and(any(Predicate.class), any(Predicate.class)))
                .thenReturn(finalPredicate);

        // Act
        Specification<Training> spec = TrainingSpecification.filterByCriteriaForTrainee(dto);
        Predicate result = spec.toPredicate(root, query, cb);

        // Assert
        assertNotNull(result);
        verify(root).join("trainee");
        verify(root).join("trainer");
        verify(root).join("trainingType");
        verify(cb, atLeastOnce()).and(any(), any());
    }

    @Test
    void filterByCriteriaForTrainer_shouldApplyAllFilters() {
        // Arrange
        GetTrainerTrainingsCriteriaFilterDTO dto = new GetTrainerTrainingsCriteriaFilterDTO();
        dto.setTrainerUsername("trainer1");
        dto.setTraineeName("john");
        dto.setTrainingType("CARDIO");
        dto.setFromDate(LocalDate.now().minusDays(10));
        dto.setToDate(LocalDate.now());

        Root<Training> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate predicate = mock(Predicate.class);

        Join trainerJoin = mock(Join.class);
        Join trainerUserJoin = mock(Join.class);
        Join traineeJoin = mock(Join.class);
        Join traineeUserJoin = mock(Join.class);
        Join typeJoin = mock(Join.class);

        Path usernamePath = mock(Path.class);
        Path trainingDatePath = mock(Path.class);

        when(cb.conjunction()).thenReturn(predicate);
        when(cb.and(any(), any())).thenReturn(predicate);

        when(root.join("trainer")).thenReturn(trainerJoin);
        when(trainerJoin.join("user")).thenReturn(trainerUserJoin);
        when(trainerUserJoin.get("username")).thenReturn(usernamePath);

        when(root.join("trainee")).thenReturn(traineeJoin);
        when(traineeJoin.join("user")).thenReturn(traineeUserJoin);
        when(traineeUserJoin.get("username")).thenReturn(usernamePath);

        when(root.get("trainingDate")).thenReturn(trainingDatePath);

        when(root.join("trainingType")).thenReturn(typeJoin);

        when(cb.equal(any(), any())).thenReturn(predicate);
        when(cb.like(any(), anyString())).thenReturn(predicate);
        when(cb.lower(any())).thenReturn(usernamePath);
        when(cb.and(any(Predicate.class), any(Predicate.class)))
                .thenReturn(predicate);
        when(cb.and(any(Predicate.class), any(Predicate.class)))
                .thenReturn(predicate);

        // Act
        Specification<Training> spec = TrainingSpecification.filterByCriteriaForTrainer(dto);
        Predicate result = spec.toPredicate(root, query, cb);

        // Assert
        assertNotNull(result);
        verify(root).join("trainer");
        verify(root).join("trainee");
        verify(cb, atLeastOnce()).and(any(), any());
    }

    @Test
    void filterByCriteria_shouldHandleNullFields() {
        // Arrange
        GetTraineeTrainingsCriteriaFilterDTO dto = new GetTraineeTrainingsCriteriaFilterDTO();
        dto.setTraineeUsername("john"); // only required field

        Root<Training> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate predicate = mock(Predicate.class);

        Join traineeJoin = mock(Join.class);
        Join traineeUserJoin = mock(Join.class);
        Path usernamePath = mock(Path.class);

        when(cb.conjunction()).thenReturn(predicate);
        when(cb.and(any(), any())).thenReturn(predicate);

        when(root.join("trainee")).thenReturn(traineeJoin);
        when(traineeJoin.join("user")).thenReturn(traineeUserJoin);
        when(traineeUserJoin.get("username")).thenReturn(usernamePath);

        when(cb.equal(any(), any())).thenReturn(predicate);

        // Act
        Specification<Training> spec = TrainingSpecification.filterByCriteriaForTrainee(dto);
        Predicate result = spec.toPredicate(root, query, cb);

        // Assert
        assertNotNull(result);
        verify(root, never()).join("trainingType"); // should NOT be called
    }
}