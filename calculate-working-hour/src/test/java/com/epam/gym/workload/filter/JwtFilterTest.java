package com.epam.gym.workload.filter;

import com.epam.gym.workload.config.security.SecurityConfig;
import com.epam.gym.workload.util.JwtUtil;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtFilterTest {

    private JwtUtil jwtUtil;
    private JwtFilter jwtFilter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtUtil = mock(JwtUtil.class);
        jwtFilter = new JwtFilter(jwtUtil);
        filterChain = mock(FilterChain.class);
    }

    @Test
    void shouldNotFilter_shouldReturnTrue_whenPathMatchesWhitelist() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath(SecurityConfig.AUTH_WHITELIST[0]); // assuming exists

        boolean result = jwtFilter.shouldNotFilter(request);

        assertTrue(result);
    }

    @Test
    void shouldNotFilter_shouldReturnFalse_whenPathNotInWhitelist() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/secure");

        boolean result = jwtFilter.shouldNotFilter(request);

        assertFalse(result);
    }

    @Test
    void doFilterInternal_shouldReturn401_whenAuthHeaderMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus());
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_shouldReturn401_whenAuthHeaderInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "InvalidToken");

        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus());
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_shouldReturn401_whenTokenInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer bad-token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        doThrow(new RuntimeException("Invalid token"))
                .when(jwtUtil).validateToken("bad-token");

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus());
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_shouldProceed_whenTokenValid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        doNothing().when(jwtUtil).validateToken("valid-token");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(jwtUtil).validateToken("valid-token");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldHandleTransactionId_whenPresent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        request.addHeader("X-Transaction-Id", "tx-123");

        MockHttpServletResponse response = new MockHttpServletResponse();

        doNothing().when(jwtUtil).validateToken("valid-token");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldHandleNullTransactionId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        doNothing().when(jwtUtil).validateToken("valid-token");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}