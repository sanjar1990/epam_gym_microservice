package com.epam.gym.service;

import com.epam.gym.dto.TrainingTypeDTO;
import com.epam.gym.entity.TrainingType;
import com.epam.gym.mapper.training_type.TrainingTypeMapperI;
import com.epam.gym.repository.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingTypeService {
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingTypeMapperI trainingTypeMapperI;

    public List<TrainingTypeDTO> getAllTrainingTypes() {
        List<TrainingType> trainingTypes = trainingTypeRepository.findAll();
        return trainingTypes.stream()
                .map(trainingTypeMapperI::toDTO)
                .toList();
    }


}
