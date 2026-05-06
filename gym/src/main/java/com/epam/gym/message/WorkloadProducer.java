package com.epam.gym.message;

import com.epam.gym.dto.TrainerWorkloadRequest;
import com.epam.gym.service.WorkloadConnectionI;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
@Primary
@Component
@RequiredArgsConstructor
public class WorkloadProducer implements WorkloadConnectionI {
    private final JmsTemplate jmsTemplate;

    public void updateWorkload(TrainerWorkloadRequest message, String token, String transactionId) {
        jmsTemplate.convertAndSend("workload.update", message, msg -> {
            msg.setStringProperty("transactionId", transactionId);
            msg.setStringProperty("token", token);
            return msg;
        });
    }
}
