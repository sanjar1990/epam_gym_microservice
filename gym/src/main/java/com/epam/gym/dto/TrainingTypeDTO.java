package com.epam.gym.dto;

import com.epam.gym.enums.TrainingTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainingTypeDTO {

    private Long id;
    private TrainingTypeEnum trainingTypeName;
}
