package com.epam.gym.workload.config.security;

import com.epam.gym.workload.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JmsSecurityService {

    private final JwtUtil jwtUtil;

    public void validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new RuntimeException("Missing JWT token");
        }
        token = token.substring(7);
        jwtUtil.validateToken(token);
    }
}