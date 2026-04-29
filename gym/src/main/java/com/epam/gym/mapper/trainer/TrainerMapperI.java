package com.epam.gym.mapper.trainer;

import com.epam.gym.dto.CreateTrainerRequestDTO;
import com.epam.gym.dto.TrainerDTO;
import com.epam.gym.dto.UpdateTrainerRequestDTO;
import com.epam.gym.entity.Trainer;
import com.epam.gym.mapper.training_type.TrainingTypeMapperI;
import com.epam.gym.mapper.user.UserMapperI;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserMapperI.class, TrainingTypeMapperI.class})
public interface TrainerMapperI {
    @Mapping(target = "traineesList", source = "trainees")
    TrainerDTO toTrainerDTO(Trainer trainer);

    @Mapping(target = "user", source = ".")
    @Mapping(target = "trainingTypeId", source = "trainingTypeId")
    @Mapping(target = "trainingType", ignore = true) // lazy relation
    @Mapping(target = "trainees", ignore = true)
        // no mapping here
    Trainer toEntity(CreateTrainerRequestDTO dto);

    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "user.isActive", source = "isActive")
    @Mapping(target = "user.username", ignore = true)
    @Mapping(target = "user.password", ignore = true)
    @Mapping(target = "trainingType", ignore = true)
    @Mapping(target = "trainees", ignore = true)
    void updateTrainerFromDto(UpdateTrainerRequestDTO dto,
                              @MappingTarget Trainer trainer);

}
