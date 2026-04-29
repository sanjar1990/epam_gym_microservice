package com.epam.gym.service.clint;

import com.epam.gym.dto.TrainerWorkloadRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class WorkloadClientFallback implements WorkloadClientService {

    @Override
    public void sendWorkload(TrainerWorkloadRequest request, String token, String transactionId) {
        log.error("Workload service is DOWN. Fallback triggered for user: {}", request.getUsername());
    }

    @Override
    public void deleteWorkload(List<Long> trainingIdList, String token, String transactionId) {
        log.error("Workload service is DOWN. Fallback triggered for transaction Id: {}", transactionId);

    }
}