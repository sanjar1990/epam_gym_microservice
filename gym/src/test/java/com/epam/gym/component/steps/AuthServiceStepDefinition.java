package com.epam.gym.component.steps;

import com.epam.gym.dto.AuthDTO;
import com.epam.gym.dto.UserChangePasswordRequestDTO;
import com.epam.gym.enums.UserRoleEnum;
import com.epam.gym.service.AuthService;
import com.epam.gym.service.JwtTokenService;
import com.epam.gym.service.LoginAttemptService;
import com.epam.gym.service.UserService;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.List;
public class AuthServiceStepDefinition {

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

    @Mock
    private Authentication authentication;

    private AuthService authService;

    private AuthDTO authDTO;
    private UserChangePasswordRequestDTO passwordRequestDTO;

    private String token;
    private Exception exception;
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

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
    @Given("a valid authentication request")
    public void a_valid_authentication_request() {
        authDTO = new AuthDTO();
        authDTO.setUsername("john");
        authDTO.setPassword("password");

        when(loginAttemptService.isBlocked("john"))
                .thenReturn(false);

        UserDetails userDetails = new User(
                "john",
                "password",
                List.of(new SimpleGrantedAuthority(UserRoleEnum.ROLE_TRAINER.name()))
        );

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(jwtTokenService.encode(
                Mockito.eq("john"),
                Mockito.anyList())
        ).thenReturn("jwt-token");
    }
    @When("the user logs in")
    public void the_user_logs_in() {
        token = authService.login(authDTO);
    }
    @Then("a JWT token should be returned")
    public void a_jwt_token_should_be_returned() {
        Assertions.assertEquals("jwt-token", token);
    }

    @And("login success should be recorded")
    public void login_success_should_be_recorded() {
        verify(loginAttemptService, times(1))
                .loginSucceeded("john");

        verify(counter, times(1)).increment();
    }

    @Given("a blocked user authentication request")
    public void a_blocked_user_authentication_request() {
        authDTO = new AuthDTO();
        authDTO.setUsername("blockedUser");
        authDTO.setPassword("password");

        when(loginAttemptService.isBlocked("blockedUser"))
                .thenReturn(true);
    }

    @When("the user tries to log in")
    public void the_user_tries_to_log_in() {
        try {
            authService.login(authDTO);
        } catch (Exception ex) {
            exception = ex;
        }
    }

    @Then("an exception with message {string} should be thrown")
    public void an_exception_with_message_should_be_thrown(String message) {
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(message, exception.getMessage());
    }

    @Given("an invalid authentication request")
    public void an_invalid_authentication_request() {
        authDTO = new AuthDTO();
        authDTO.setUsername("john");
        authDTO.setPassword("wrong-password");

        when(loginAttemptService.isBlocked("john"))
                .thenReturn(false);

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));
    }

    @And("login failure should be recorded")
    public void login_failure_should_be_recorded() {
        verify(loginAttemptService, times(1))
                .loginFailed("john");
    }

    @Given("a valid change password request")
    public void a_valid_change_password_request() {
        passwordRequestDTO = new UserChangePasswordRequestDTO();
    }

    @When("the password is changed")
    public void the_password_is_changed() {
        authService.changePassword(passwordRequestDTO);
    }

    @Then("the password should be updated")
    public void the_password_should_be_updated() {
        verify(userService, times(1))
                .changePassword(passwordRequestDTO);
    }
}