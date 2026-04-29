package com.epam.gym.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPT = 3;
    private static final long BLOCK_TIME_MS = 5 * 60 * 1000; // 5 minutes

    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, Long> blockTimeCache = new ConcurrentHashMap<>();


    public void loginFailed(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0) + 1;
        attemptsCache.put(username, attempts);

        if (attempts >= MAX_ATTEMPT) {
            blockTimeCache.put(username, System.currentTimeMillis());
        }
    }


    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        blockTimeCache.remove(username);
    }


    public boolean isBlocked(String username) {
        if (!blockTimeCache.containsKey(username)) {
            return false;
        }

        long blockTime = blockTimeCache.get(username);
        long now = System.currentTimeMillis();

        if (now - blockTime > BLOCK_TIME_MS) {
            // ⏳ unblock after time passed
            blockTimeCache.remove(username);
            attemptsCache.remove(username);
            return false;
        }

        return true;
    }
}