package com.epam.gym.mapper.training_type;

import com.epam.gym.dto.TrainingTypeDTO;
import com.epam.gym.entity.TrainingType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapperI {
    TrainingTypeDTO toDTO(TrainingType trainingType);
}
