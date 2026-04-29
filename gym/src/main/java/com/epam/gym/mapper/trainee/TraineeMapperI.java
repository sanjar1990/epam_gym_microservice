package com.epam.gym.mapper.trainee;

import com.epam.gym.dto.CreateTraineeRequestDTO;
import com.epam.gym.dto.TraineeDTO;
import com.epam.gym.dto.UpdateTraineeRequestDTO;
import com.epam.gym.entity.Trainee;
import com.epam.gym.mapper.trainer.TrainerMapperI;
import com.epam.gym.mapper.user.UserMapperI;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserMapperI.class, TrainerMapperI.class})
public interface TraineeMapperI {
    @Mapping(target = "user", source = ".")
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    Trainee toTrainee(CreateTraineeRequestDTO dto);

    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "user.isActive", source = "isActive")
    @Mapping(target = "user.username", ignore = true)
    @Mapping(target = "user.password", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    void updateTraineeFromDto(UpdateTraineeRequestDTO dto,
                              @MappingTarget Trainee trainee);

    @Mapping(target = "trainersList", source = "trainers")
    TraineeDTO toTraineeDTO(Trainee trainee);

}
