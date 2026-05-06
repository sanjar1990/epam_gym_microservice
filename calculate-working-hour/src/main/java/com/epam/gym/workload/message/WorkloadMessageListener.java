package com.epam.gym.workload.message;

import com.epam.gym.workload.config.security.JmsSecurityService;
import com.epam.gym.workload.dto.TrainerWorkloadRequest;
import com.epam.gym.workload.service.WorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkloadMessageListener {
    private final WorkloadService workloadService;
    private final JmsSecurityService jmsSecurityService;

    @JmsListener(destination = "workload.update")
    public void updateWorkload(@Payload TrainerWorkloadRequest message,
                               @Header(name = "token") String token,
                               @Header(name = "transactionId") String transactionId) {

        try {
            log.info("TransactionId: {}", transactionId);
            jmsSecurityService.validateToken(token);
            workloadService.updateWorkload(message);
        } catch (RuntimeException e) {
            log.error("Failed to update workload: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
