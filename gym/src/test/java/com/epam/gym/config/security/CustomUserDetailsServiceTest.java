package com.epam.gym.config.security;

import com.epam.gym.entity.User;
import com.epam.gym.enums.UserRoleEnum;
import com.epam.gym.repository.UserRepository;
import com.epam.gym.service.UserRoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleService userRoleService;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setPassword("1234");
        user.setIsActive(true);

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(userRoleService.getByProfileId(1L))
                .thenReturn(List.of(UserRoleEnum.ROLE_ADMIN));

        UserDetails result = service.loadUserByUsername("john");

        assertNotNull(result);
        assertEquals("john", result.getUsername());
        assertEquals("1234", result.getPassword());
        assertEquals(1, result.getAuthorities().size());

        verify(userRepository).findByUsername("john");
        verify(userRoleService).getByProfileId(1L);
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                service.loadUserByUsername("john")
        );

        verify(userRepository).findByUsername("john");
        verify(userRoleService, never()).getByProfileId(any());
    }
}