package com.epam.gym.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginAttemptServiceTest {

    private LoginAttemptService service;

    @BeforeEach
    void setUp() {
        service = new LoginAttemptService();
    }

    @Test
    void loginFailed_shouldIncreaseAttempts_andNotBlockBeforeLimit() {
        String user = "john";

        service.loginFailed(user);
        service.loginFailed(user);

        assertFalse(service.isBlocked(user));
    }

    @Test
    void loginFailed_shouldBlockUser_afterMaxAttempts() {
        String user = "john";

        service.loginFailed(user);
        service.loginFailed(user);
        service.loginFailed(user);

        assertTrue(service.isBlocked(user));
    }

    @Test
    void loginSucceeded_shouldResetAttemptsAndUnblockUser() {
        String user = "john";

        service.loginFailed(user);
        service.loginFailed(user);
        service.loginFailed(user);

        assertTrue(service.isBlocked(user));

        service.loginSucceeded(user);

        assertFalse(service.isBlocked(user));
    }

    @Test
    void isBlocked_shouldReturnFalse_whenUserNeverBlocked() {
        assertFalse(service.isBlocked("unknown"));
    }

    @Test
    void isBlocked_shouldUnblockUser_afterBlockTimeExpires() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        String user = "john";

        service.loginFailed(user);
        service.loginFailed(user);
        service.loginFailed(user);

        assertTrue(service.isBlocked(user));

        Thread.sleep(10);

        var blockTimeField = service.getClass().getDeclaredField("blockTimeCache");
        blockTimeField.setAccessible(true);

        var map = (java.util.Map<String, Long>) blockTimeField.get(service);
        map.put(user, System.currentTimeMillis() - (6 * 60 * 1000));

        assertFalse(service.isBlocked(user));
    }

    @Test
    void loginFailed_shouldTrackAttemptsIndependently_perUser() {
        String user1 = "john";
        String user2 = "alice";

        service.loginFailed(user1);
        service.loginFailed(user1);
        service.loginFailed(user1);

        service.loginFailed(user2);

        assertTrue(service.isBlocked(user1));
        assertFalse(service.isBlocked(user2));
    }

    @Test
    void loginSucceeded_shouldOnlyAffectSpecificUser() {
        String user1 = "john";
        String user2 = "alice";

        for (int i = 0; i < 3; i++) {
            service.loginFailed(user1);
            service.loginFailed(user2);
        }

        assertTrue(service.isBlocked(user1));
        assertTrue(service.isBlocked(user2));

        service.loginSucceeded(user1);

        assertFalse(service.isBlocked(user1));
        assertTrue(service.isBlocked(user2));
    }

    @Test
    void loginFailed_shouldKeepUserBlocked_afterMoreThanMaxAttempts() {
        String user = "john";

        for (int i = 0; i < 5; i++) {
            service.loginFailed(user);
        }

        assertTrue(service.isBlocked(user));
    }
}