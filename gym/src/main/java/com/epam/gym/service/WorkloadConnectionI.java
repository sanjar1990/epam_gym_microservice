package com.epam.gym.service;

import com.epam.gym.dto.TrainerWorkloadRequest;

public interface WorkloadConnectionI {
     void updateWorkload(TrainerWorkloadRequest message, String token, String transactionId);
}
