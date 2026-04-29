package com.epam.gym.util;

import com.epam.gym.config.security.CustomUserDetails;
import com.epam.gym.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class SpringSecurityUtil {

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails;
        if (authentication != null) {
            customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        } else {
            throw new SecurityException("User not found");
        }
        if (customUserDetails != null) {
            return customUserDetails.getUser();
        } else {
            throw new SecurityException("User not found");
        }

    }

    public static Long getCurrentUserId() {
        Object principal = Objects
                .requireNonNull(
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication())
                .getPrincipal();
        if (principal instanceof CustomUserDetails user) {
            return user.getUser().getId();
        }
        return null;
    }

}