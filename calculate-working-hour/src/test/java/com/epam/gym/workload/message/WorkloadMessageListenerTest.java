package com.epam.gym.workload.message;

import com.epam.gym.workload.config.security.JmsSecurityService;
import com.epam.gym.workload.dto.TrainerWorkloadRequest;
import com.epam.gym.workload.service.WorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class WorkloadMessageListenerTest {

    private WorkloadService workloadService;
    private JmsSecurityService jmsSecurityService;
    private WorkloadMessageListener listener;

    @BeforeEach
    void setUp() {
        workloadService = mock(WorkloadService.class);
        jmsSecurityService = mock(JmsSecurityService.class);
        listener = new WorkloadMessageListener(workloadService, jmsSecurityService);
    }

    @Test
    void updateWorkload_shouldProcessSuccessfully() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

        listener.updateWorkload(request, "token", "tx-1");

        verify(jmsSecurityService).validateToken("token");
        verify(workloadService).updateWorkload(request);
    }

    @Test
    void updateWorkload_shouldThrow_whenTokenInvalid() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

        doThrow(new RuntimeException("Invalid token"))
                .when(jmsSecurityService).validateToken("bad-token");

        assertThrows(RuntimeException.class, () ->
                listener.updateWorkload(request, "bad-token", "tx-1")
        );

        verify(workloadService, never()).updateWorkload(any());
    }

    @Test
    void updateWorkload_shouldThrow_whenServiceFails() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

        doNothing().when(jmsSecurityService).validateToken("token");
        doThrow(new RuntimeException("DB error"))
                .when(workloadService).updateWorkload(request);

        assertThrows(RuntimeException.class, () ->
                listener.updateWorkload(request, "token", "tx-1")
        );
    }

    @Test
    void updateWorkload_shouldThrow_whenTokenIsNull() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

        doThrow(new RuntimeException("Token is null"))
                .when(jmsSecurityService).validateToken(null);

        assertThrows(RuntimeException.class, () ->
                listener.updateWorkload(request, null, "tx-1")
        );

        verify(workloadService, never()).updateWorkload(any());
    }

    @Test
    void updateWorkload_shouldThrow_whenMessageIsNull() {
        doNothing().when(jmsSecurityService).validateToken("token");

        doThrow(new RuntimeException("Message is null"))
                .when(workloadService).updateWorkload(null);

        assertThrows(RuntimeException.class, () ->
                listener.updateWorkload(null, "token", "tx-1")
        );
    }

    @Test
    void updateWorkload_shouldWrapException() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

        RuntimeException original = new RuntimeException("boom");

        doThrow(original).when(jmsSecurityService).validateToken("token");

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                listener.updateWorkload(request, "token", "tx-1")
        );

        assertThrows(RuntimeException.class, () -> { throw thrown.getCause(); });
    }

    @Test
    void updateWorkload_shouldCallMethodsInOrder() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

        listener.updateWorkload(request, "token", "tx-1");

        var inOrder = inOrder(jmsSecurityService, workloadService);

        inOrder.verify(jmsSecurityService).validateToken("token");
        inOrder.verify(workloadService).updateWorkload(request);
    }
}