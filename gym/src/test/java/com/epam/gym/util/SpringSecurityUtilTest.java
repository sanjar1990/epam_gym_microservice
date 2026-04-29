package com.epam.gym.util;

import com.epam.gym.config.security.CustomUserDetails;
import com.epam.gym.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpringSecurityUtilTest {

    @Test
    void getCurrentUser() {
        // Arrange
        User user = new User();
        user.setId(1L);

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUser()).thenReturn(user);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        User result = SpringSecurityUtil.getCurrentUser();

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCurrentUserId() {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUser()).thenReturn(null);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThrows(NullPointerException.class,
                SpringSecurityUtil::getCurrentUserId);
    }
}