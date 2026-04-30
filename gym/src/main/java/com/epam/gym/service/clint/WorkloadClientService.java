package com.epam.gym.service.clint;

import com.epam.gym.dto.TrainerWorkloadRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "workload-service", url = "localhost:8082", fallback = WorkloadClientFallback.class)
public interface WorkloadClientService {

    @PostMapping("/api/v1/workload")
    void updateWorkload(@RequestBody TrainerWorkloadRequest request,
                      @RequestHeader("Authorization") String token,
                      @RequestHeader("X-Transaction-Id") String transactionId);

}
