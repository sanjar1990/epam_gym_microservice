package com.epam.gym.workload.filter;

import com.epam.gym.workload.config.security.SecurityConfig;
import com.epam.gym.workload.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@NullMarked

public class JwtFilter extends OncePerRequestFilter {


    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return Arrays.stream(SecurityConfig.AUTH_WHITELIST)
                .anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String transactionId = request.getHeader("X-Transaction-Id");


        long start = System.currentTimeMillis();
        if (transactionId != null) {
            log.info("X-Transaction-Id: {}", transactionId);
            log.info("[TRANSACTION START] {} {}", request.getMethod(), request.getRequestURI());


            long time = System.currentTimeMillis() - start;
            System.out.println("URI:" + request.getRequestURI());
            log.info("[TRANSACTION END] {} {} | status={} | time={}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    time);
        } else {

            System.out.println("header is null");
        }
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            String token = authHeader.substring(7);
            JwtUtil.validateToken(token);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}