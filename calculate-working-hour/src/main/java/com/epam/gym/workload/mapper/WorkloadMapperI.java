package com.epam.gym.workload.mapper;

import com.epam.gym.workload.dto.TrainerWorkloadRequest;
import com.epam.gym.workload.entity.Workload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkloadMapperI {
    Workload toEntity(TrainerWorkloadRequest request);
}
