package com.epam.gym.service;

import com.epam.gym.dto.JwtDTO;
import com.epam.gym.enums.UserRoleEnum;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenService();

        ReflectionTestUtils.setField(jwtTokenService,
                "DEFAULT_EXPIRATION_MS",
                1000 * 60 * 60);

        ReflectionTestUtils.setField(jwtTokenService,
                "secretkey",
                "dGVzdHNlY3JldGtleXRlc3RzZWNyZXRrZXl0ZXN0c2VjcmV0a2V5MTIz");
    }

    @Test
    void encode_and_decode_shouldWorkCorrectly() {
        String token = jwtTokenService.encode(
                "john",
                List.of(UserRoleEnum.ROLE_TRAINEE)
        );

        JwtDTO decoded = jwtTokenService.decode(token);

        assertAll(
                () -> assertNotNull(token),
                () -> assertEquals("john", decoded.getUsername()),
                () -> assertEquals(1, decoded.getRole().size()),
                () -> assertTrue(decoded.getRole().contains(UserRoleEnum.ROLE_TRAINEE))
        );
    }

    @Test
    void encode_and_decode_shouldHandleMultipleRoles() {
        String token = jwtTokenService.encode(
                "john",
                List.of(UserRoleEnum.ROLE_TRAINEE, UserRoleEnum.ROLE_TRAINER)
        );

        JwtDTO decoded = jwtTokenService.decode(token);

        assertAll(
                () -> assertEquals(2, decoded.getRole().size()),
                () -> assertTrue(decoded.getRole().contains(UserRoleEnum.ROLE_TRAINEE)),
                () -> assertTrue(decoded.getRole().contains(UserRoleEnum.ROLE_TRAINER))
        );
    }

    @Test
    void extractUserName_shouldReturnCorrectUsername() {
        String token = jwtTokenService.encode("john", List.of());

        String username = jwtTokenService.extractUserName(token);

        assertEquals("john", username);
    }


    @Test
    void decode_shouldThrowException_whenTokenExpired() throws InterruptedException {
        ReflectionTestUtils.setField(jwtTokenService, "DEFAULT_EXPIRATION_MS", 1);

        String token = jwtTokenService.encode("john", List.of());

        Thread.sleep(5);

        assertThrows(ExpiredJwtException.class,
                () -> jwtTokenService.decode(token));
    }

    @Test
    void decode_shouldThrowException_whenTokenInvalid() {
        assertThrows(RuntimeException.class,
                () -> jwtTokenService.decode("invalid.token.value"));
    }

    @Test
    void decode_shouldHandleEmptyRoles() {
        String token = jwtTokenService.encode("john", List.of());

        JwtDTO decoded = jwtTokenService.decode(token);

        assertNotNull(decoded.getRole());
        assertTrue(decoded.getRole().isEmpty());
    }

    @Test
    void decode_shouldThrowException_forTamperedToken() {
        String token = jwtTokenService.encode(
                "john",
                List.of(UserRoleEnum.ROLE_TRAINEE)
        );

        String brokenToken = token.substring(0, token.length() - 2) + "xx";

        assertThrows(Exception.class,
                () -> jwtTokenService.decode(brokenToken));
    }

    @Test
    void encode_shouldGenerateDifferentTokens_forDifferentUsers() {
        String token1 = jwtTokenService.encode("john", List.of());
        String token2 = jwtTokenService.encode("alice", List.of());

        assertNotEquals(token1, token2);
    }

    @Test
    void encode_shouldGenerateDifferentTokens_forDifferentRoles() {
        String token1 = jwtTokenService.encode("john",
                List.of(UserRoleEnum.ROLE_TRAINEE));

        String token2 = jwtTokenService.encode("john",
                List.of(UserRoleEnum.ROLE_TRAINER));

        assertNotEquals(token1, token2);
    }
}