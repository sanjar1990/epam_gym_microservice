package com.epam.gym.mapper.training;

import com.epam.gym.dto.CreateTrainingDTO;
import com.epam.gym.dto.TraineeTrainingResponseDTO;
import com.epam.gym.dto.TrainerTrainingResponseDTO;
import com.epam.gym.entity.Training;
import com.epam.gym.mapper.trainer.TrainerMapperI;
import com.epam.gym.mapper.training_type.TrainingTypeMapperI;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TrainerMapperI.class, TrainingTypeMapperI.class})
public interface TrainingMapperI {
    @Mapping(target = "trainee", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "trainingType", ignore = true)
    Training toEntity(CreateTrainingDTO dto);

    @Mapping(target = "trainer", source = "trainer")
    @Mapping(target = "trainingType", source = "trainingType")
    @Mapping(target = "trainingName", source = "trainingType.trainingTypeName")
    TraineeTrainingResponseDTO toTraineeTrainingResponseDTO(Training training);

    @Mapping(target = "trainingName", source = "trainingType.trainingTypeName")
    @Mapping(target = "trainee", source = "trainee")
    TrainerTrainingResponseDTO toTrainerTrainingResponseDTO(Training training);
}
