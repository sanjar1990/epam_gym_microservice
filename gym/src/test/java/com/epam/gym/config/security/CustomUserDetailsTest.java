package com.epam.gym.config.security;

import com.epam.gym.entity.User;
import com.epam.gym.enums.UserRoleEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsTest {

    @Test
    void constructor_shouldMapRolesToAuthorities() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("1234");
        user.setIsActive(true);

        List<UserRoleEnum> roles = List.of(
                UserRoleEnum.ROLE_TRAINER,
                UserRoleEnum.ROLE_ADMIN
        );

        CustomUserDetails details = new CustomUserDetails(user, roles);

        assertEquals(2, details.getAuthorities().size());
        assertTrue(details.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_TRAINER")));
        assertTrue(details.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void constructor_withEmptyRoles_shouldCreateEmptyAuthorities() {
        CustomUserDetails details =
                new CustomUserDetails(new User(), List.of());

        assertNotNull(details.getAuthorities());
        assertTrue(details.getAuthorities().isEmpty());
    }


    @Test
    void getUsername_shouldReturnUsernameFromUser() {
        User user = new User();
        user.setUsername("john");

        CustomUserDetails details =
                new CustomUserDetails(user, List.of());

        assertEquals("john", details.getUsername());
    }

    @Test
    void getPassword_shouldReturnPasswordFromUser() {
        User user = new User();
        user.setPassword("secret");

        CustomUserDetails details =
                new CustomUserDetails(user, List.of());

        assertEquals("secret", details.getPassword());
    }

    @Test
    void getUser_shouldReturnSameUserInstance() {
        User user = new User();

        CustomUserDetails details =
                new CustomUserDetails(user, List.of());

        assertEquals(user, details.getUser());
    }

    @Test
    void getRoleList_shouldReturnMappedAuthorities() {
        List<UserRoleEnum> roles = List.of(UserRoleEnum.ROLE_ADMIN);

        CustomUserDetails details =
                new CustomUserDetails(new User(), roles);

        assertEquals(1, details.getRoleList().size());
        assertEquals("ROLE_ADMIN",
                details.getRoleList().get(0).getAuthority());
    }

    @Test
    void isAccountNonLocked_shouldReturnFalse_whenUserInactive() {
        User user = new User();
        user.setIsActive(false);

        CustomUserDetails details =
                new CustomUserDetails(user, List.of());

        assertFalse(details.isAccountNonLocked());
    }

    @Test
    void isAccountNonLocked_shouldReturnTrue_whenUserActive() {
        User user = new User();
        user.setIsActive(true);

        CustomUserDetails details =
                new CustomUserDetails(user, List.of());

        assertTrue(details.isAccountNonLocked());
    }

    @Test
    void isAccountNonExpired_shouldAlwaysReturnTrue() {
        CustomUserDetails details =
                new CustomUserDetails(new User(), List.of());

        assertTrue(details.isAccountNonExpired());
    }

    @Test
    void isCredentialsNonExpired_shouldAlwaysReturnTrue() {
        CustomUserDetails details =
                new CustomUserDetails(new User(), List.of());

        assertTrue(details.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_shouldAlwaysReturnTrue() {
        CustomUserDetails details =
                new CustomUserDetails(new User(), List.of());

        assertTrue(details.isEnabled());
    }
}