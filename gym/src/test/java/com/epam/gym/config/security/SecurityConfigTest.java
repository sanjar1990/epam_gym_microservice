package com.epam.gym.config.security;

import com.epam.gym.service.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",

        "spring.flyway.enabled=false",
        "spring.liquibase.enabled=false"
})
class SecurityConfigTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @MockitoBean
    private JwtTokenService jwtTokenService;

    @Test
    void context_shouldLoadSuccessfully() {
        assertNotNull(context);
    }

    @Test
    void passwordEncoder_shouldWork() {
        String raw = "1234";
        String encoded = passwordEncoder.encode(raw);

        assertTrue(passwordEncoder.matches(raw, encoded));
    }

    @Test
    void authenticationProvider_shouldBePresent() {
        assertNotNull(authenticationProvider);
    }
}