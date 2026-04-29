package com.epam.gym.service;

import com.epam.gym.dto.AuthDTO;
import com.epam.gym.dto.UserChangePasswordRequestDTO;
import com.epam.gym.enums.UserRoleEnum;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private MeterRegistry meterRegistry;
    @Mock
    private Counter counter;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup() {
        when(meterRegistry.counter("user.login.count"))
                .thenReturn(counter);

        authService = new AuthService(
                userService,
                jwtTokenService,
                meterRegistry,
                authenticationManager,
                loginAttemptService
        );
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        AuthDTO dto = new AuthDTO("john", "1234");

        when(loginAttemptService.isBlocked("john")).thenReturn(false);

        User userDetails = new User(
                "john",
                "1234",
                List.of(new SimpleGrantedAuthority("ROLE_TRAINEE"))
        );

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenService.encode(eq("john"), any()))
                .thenReturn("token");

        String result = authService.login(dto);

        assertEquals("token", result);
        verify(loginAttemptService).loginSucceeded("john");
        verify(counter).increment();
    }

    @Test
    void login_shouldThrowException_whenUserIsBlocked() {
        AuthDTO dto = new AuthDTO("john", "1234");

        when(loginAttemptService.isBlocked("john")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(dto));

        assertEquals("User is blocked. Try again later.", ex.getMessage());

        verifyNoInteractions(authenticationManager, jwtTokenService);
    }

    @Test
    void login_shouldThrowException_whenAuthenticationFails() {
        AuthDTO dto = new AuthDTO("john", "wrong");

        when(loginAttemptService.isBlocked("john")).thenReturn(false);

        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(dto));

        assertEquals("Invalid username or password", ex.getMessage());
        verify(loginAttemptService).loginFailed("john");
        verify(counter, never()).increment();
    }

    @Test
    void login_shouldMapSingleRole() {
        AuthDTO dto = new AuthDTO("john", "1234");

        when(loginAttemptService.isBlocked("john")).thenReturn(false);

        User userDetails = new User(
                "john",
                "1234",
                List.of(new SimpleGrantedAuthority("ROLE_TRAINER"))
        );

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenService.encode(any(), any()))
                .thenReturn("token");

        authService.login(dto);

        verify(jwtTokenService).encode(eq("john"),
                eq(List.of(UserRoleEnum.ROLE_TRAINER)));
    }

    @Test
    void login_shouldMapMultipleRoles() {
        AuthDTO dto = new AuthDTO("john", "1234");

        when(loginAttemptService.isBlocked("john")).thenReturn(false);

        User userDetails = new User(
                "john",
                "1234",
                List.of(
                        new SimpleGrantedAuthority("ROLE_TRAINER"),
                        new SimpleGrantedAuthority("ROLE_TRAINEE")
                )
        );

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenService.encode(any(), any()))
                .thenReturn("token");

        authService.login(dto);

        verify(jwtTokenService).encode(eq("john"),
                argThat(roles ->
                        roles.size() == 2 &&
                                roles.contains(UserRoleEnum.ROLE_TRAINER) &&
                                roles.contains(UserRoleEnum.ROLE_TRAINEE)
                ));
    }

    @Test
    void login_shouldHandleEmptyAuthorities() {
        AuthDTO dto = new AuthDTO("john", "1234");

        when(loginAttemptService.isBlocked("john")).thenReturn(false);

        User userDetails = new User("john", "1234", List.of());

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenService.encode(any(), any()))
                .thenReturn("token");

        String result = authService.login(dto);

        assertEquals("token", result);
    }

    @Test
    void login_shouldFail_whenRoleIsInvalidEnum() {
        AuthDTO dto = new AuthDTO("john", "1234");

        when(loginAttemptService.isBlocked("john")).thenReturn(false);

        User userDetails = new User(
                "john",
                "1234",
                List.of(new SimpleGrantedAuthority("INVALID_ROLE"))
        );

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        assertThrows(RuntimeException.class,
                () -> authService.login(dto));
    }

    @Test
    void login_shouldFail_whenPrincipalIsInvalid() {
        AuthDTO dto = new AuthDTO("john", "Toshiba_1990$");

        when(loginAttemptService.isBlocked("john")).thenReturn(false);

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("INVALID");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(dto));

        assertEquals("Invalid username or password", ex.getMessage());

        verify(loginAttemptService).loginFailed("john");
    }

    @Test
    void login_shouldCallMethodsInCorrectOrder() {
        AuthDTO dto = new AuthDTO("john", "1234");

        when(loginAttemptService.isBlocked("john")).thenReturn(false);

        User userDetails = new User(
                "john",
                "1234",
                List.of(new SimpleGrantedAuthority("ROLE_TRAINEE"))
        );

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenService.encode(any(), any()))
                .thenReturn("token");

        authService.login(dto);

        InOrder inOrder = inOrder(loginAttemptService, counter);

        inOrder.verify(loginAttemptService).loginSucceeded("john");
        inOrder.verify(counter).increment();
    }

    @Test
    void changePassword_shouldCallUserService() {
        UserChangePasswordRequestDTO dto = new UserChangePasswordRequestDTO();

        authService.changePassword(dto);

        verify(userService).changePassword(dto);
    }

    @Test
    void logout_shouldExecute() {
        authService.logout("token");
    }
}