package com.epam.gym.workload.config.security;

import com.epam.gym.workload.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JmsSecurityServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private JmsSecurityService jmsSecurityService;

    private static final String VALID_TOKEN =
            "Bearer jwt-token-value";

    @BeforeEach
    void setUp() {
    }

    @Test
    void validateToken_shouldCallJwtUtilValidateToken_whenTokenIsValid() {

        jmsSecurityService.validateToken(VALID_TOKEN);

        verify(jwtUtil)
                .validateToken("jwt-token-value");
    }

    @Test
    void validateToken_shouldThrowException_whenTokenIsNull() {

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> jmsSecurityService.validateToken(null)
        );

        assertEquals(
                "Missing JWT token",
                exception.getMessage()
        );
    }

    @Test
    void validateToken_shouldThrowException_whenTokenIsBlank() {

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> jmsSecurityService.validateToken(" ")
        );

        assertEquals(
                "Missing JWT token",
                exception.getMessage()
        );
    }

    @Test
    void validateToken_shouldPassTokenWithoutBearerPrefixToJwtUtil() {

        String token = "Bearer abc.def.ghi";

        jmsSecurityService.validateToken(token);

        verify(jwtUtil)
                .validateToken("abc.def.ghi");
    }

    @Test
    void validateToken_shouldPassEmptyTokenToJwtUtil_whenTokenHasOnlyBearerPrefix() {

        String token = "Bearer ";

        jmsSecurityService.validateToken(token);

        verify(jwtUtil).validateToken("");
    }
}