package com.epam.gym.config.logging;

import com.epam.gym.filter.TransactionFilter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class TransactionFilterTest {

    private final TransactionFilter filter = new TransactionFilter();

    @Test
    void shouldAddTransactionIdToMDC() throws Exception {
        TransactionFilter filter = new TransactionFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> {
            assertNotNull(MDC.get("transactionId"));
        };

        filter.doFilter(request, response, chain);
    }

    @Test
    void shouldRemoveTransactionIdAfterRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> {
            assertNotNull(MDC.get("transactionId"));
        };

        filter.doFilter(request, response, chain);

        assertNull(MDC.get("transactionId"));
    }

}