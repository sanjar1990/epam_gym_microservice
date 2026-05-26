package com.epam.gym.message;

import com.epam.gym.dto.TrainerWorkloadRequest;
import jakarta.jms.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkloadProducerTest {

    private JmsTemplate jmsTemplate;
    private WorkloadProducer workloadProducer;

    @BeforeEach
    void setUp() {
        jmsTemplate = mock(JmsTemplate.class);
        workloadProducer = new WorkloadProducer(jmsTemplate);
    }

    @Test
    void updateWorkload_shouldSendMessageWithCorrectProperties() throws Exception {
        // given
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        String token = "test-token";
        String transactionId = "tx-123";

        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        ArgumentCaptor<org.springframework.jms.core.MessagePostProcessor> processorCaptor =
                ArgumentCaptor.forClass(org.springframework.jms.core.MessagePostProcessor.class);

        // when
        workloadProducer.updateWorkload(request, token, transactionId);

        // then
        verify(jmsTemplate).convertAndSend(
                destinationCaptor.capture(),
                payloadCaptor.capture(),
                processorCaptor.capture()
        );

        assertEquals("workload.update", destinationCaptor.getValue());
        assertEquals(request, payloadCaptor.getValue());

        Message jmsMessage = mock(Message.class);
        processorCaptor.getValue().postProcessMessage(jmsMessage);

        verify(jmsMessage).setStringProperty("transactionId", transactionId);
        verify(jmsMessage).setStringProperty("token", token);
    }

    @Test
    void updateWorkload_shouldHandleNullTokenAndTransactionId() throws Exception {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

        ArgumentCaptor<org.springframework.jms.core.MessagePostProcessor> processorCaptor =
                ArgumentCaptor.forClass(org.springframework.jms.core.MessagePostProcessor.class);

        workloadProducer.updateWorkload(request, null, null);

        verify(jmsTemplate).convertAndSend(
                eq("workload.update"),
                eq(request),
                processorCaptor.capture()
        );

        Message jmsMessage = mock(Message.class);
        processorCaptor.getValue().postProcessMessage(jmsMessage);

        verify(jmsMessage).setStringProperty("transactionId", null);
        verify(jmsMessage).setStringProperty("token", null);
    }

    @Test
    void updateWorkload_shouldSendEvenIfMessageIsNull() {
        workloadProducer.updateWorkload(null, "token", "tx");

        verify(jmsTemplate).convertAndSend(
                eq("workload.update"),
                isNull(),
                any()
        );
    }

    @Test
    void updateWorkload_shouldCallJmsTemplateOnce() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

        workloadProducer.updateWorkload(request, "token", "tx");

        verify(jmsTemplate, times(1))
                .convertAndSend(anyString(), any(), any());
    }
}