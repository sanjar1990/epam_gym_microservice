package com.epam.gym.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class TransactionFilter extends OncePerRequestFilter {


    public static final String HEADER = "X-Transaction-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String transactionId = UUID.randomUUID().toString();

        long start = System.currentTimeMillis();

        MDC.put("transactionId", transactionId);

        response.setHeader(HEADER, transactionId);

        try {
            log.info("[TRANSACTION START] {} {}", request.getMethod(), request.getRequestURI());
            log.info("[TRANSACTION ID] {}", transactionId);

            filterChain.doFilter(request, response);

            long time = System.currentTimeMillis() - start;

            log.info("[TRANSACTION END] {} {} | status={} | time={}ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    time);

        } finally {
            MDC.clear();
        }
    }
}