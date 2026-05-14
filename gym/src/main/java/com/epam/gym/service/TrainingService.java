package com.epam.gym.service;

import com.epam.gym.dto.*;
import com.epam.gym.entity.Trainee;
import com.epam.gym.entity.Trainer;
import com.epam.gym.entity.Training;
import com.epam.gym.enums.ActionType;
import com.epam.gym.mapper.training.TrainingMapperI;
import com.epam.gym.message.WorkloadProducer;
import com.epam.gym.repository.TrainingRepository;
import com.epam.gym.specification.TrainingSpecification;
import com.epam.gym.util.SpringSecurityUtil;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class TrainingService {
    private final TrainingRepository trainingRepository;
    private final @Lazy TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingMapperI trainingMapperI;
    private final WorkloadConnectionI workloadConnection;


    //16. Add training.
    @Timed(value = "training.create.time", description = "Time to create training")
    @Counted(value = "training.create.count", description = "Count training creation")
    public Long addTraining(CreateTrainingDTO dto, String token) {


        Trainee trainee = traineeService.getTrainee(dto.getTraineeUsername());
        Trainer trainer = trainerService.getTrainerEntityByUsername(SpringSecurityUtil.getCurrentUser().getUsername());
        //Check
        if (!trainer.getTrainingType().getId().equals(dto.getTrainingTypeId())) {
            throw new RuntimeException("Training type id is not match");
        }

        Training training = trainingMapperI.toEntity(dto);

        training.setTrainee(trainee);
        training.setTrainer(trainer);

        trainee.getTrainings().add(training);
        trainee.getTrainers().add(trainer);
        trainer.getTrainees().add(trainee);

        trainingRepository.save(training);
        toWorkloadService(trainer, training, ActionType.ADD, token);
        log.info("Training added ID: {}", training.getId());
        return training.getId();
    }

    //    14. Get Trainee Trainings List by trainee username and criteria
//            (from date, to date, trainer name, training type).
    public List<TraineeTrainingResponseDTO> getTrainingsByTraineeUsernameCriteria(
            GetTraineeTrainingsCriteriaFilterDTO dto) {
        Specification<Training> spec = TrainingSpecification.filterByCriteriaForTrainee(dto);

        return trainingRepository.findAll(spec)
                .stream()
                .map(trainingMapperI::toTraineeTrainingResponseDTO)
                .toList();
    }

    //    15. Get Trainer Trainings List by trainer username and criteria (from date, to date, trainee name).
    public List<TrainerTrainingResponseDTO> getTrainingsByTrainerUsernameCriteria(
            GetTrainerTrainingsCriteriaFilterDTO dto) {
        Specification<Training> spec = TrainingSpecification.filterByCriteriaForTrainer(dto);
        return trainingRepository.findAll(spec)
                .stream()
                .map(trainingMapperI::toTrainerTrainingResponseDTO)
                .toList();
    }

    public Long getTrainingsCount() {
        return trainingRepository.count();
    }

    public void deleteTraining(Long trainingId, String token) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Training not found"));
        trainingRepository.deleteById(trainingId);
        toWorkloadService(training.getTrainer(), training, ActionType.DELETE, token);
        log.info("Training deleted {}", trainingId);
    }

    public void deleteTraining(Training training, String token) {
        trainingRepository.deleteById(training.getId());
        toWorkloadService(training.getTrainer(), training, ActionType.DELETE, token);
        log.info("Training deleted {}", training.getId());
    }

    private void toWorkloadService(Trainer trainer, Training training, ActionType actionType, String token) {
        TrainerWorkloadRequest trainerWorkloadRequest = new TrainerWorkloadRequest();
        trainerWorkloadRequest.setTrainerUsername(trainer.getUser().getUsername());
        trainerWorkloadRequest.setFirstName(trainer.getUser().getFirstName());
        trainerWorkloadRequest.setLastName(trainer.getUser().getLastName());
        trainerWorkloadRequest.setTrainingDate(training.getTrainingDate());
        trainerWorkloadRequest.setTrainingDuration(training.getTrainingDuration());
        trainerWorkloadRequest.setActionType(actionType);
        trainerWorkloadRequest.setStatus(trainer.getUser().getIsActive());
        workloadConnection.updateWorkload(trainerWorkloadRequest, token, MDC.get("transactionId"));
    }


}
