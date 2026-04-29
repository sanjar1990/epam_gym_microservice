package com.epam.gym.service;

import com.epam.gym.dto.AuthDTO;
import com.epam.gym.dto.UserChangePasswordRequestDTO;
import com.epam.gym.enums.UserRoleEnum;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j

public class AuthService {
    private final UserService userService;
    private final Counter userLogedInCounter;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;

    @Autowired
    public AuthService(UserService userService, JwtTokenService jwtTokenService,
                       MeterRegistry registry,
                       AuthenticationManager authenticationManager,
                       LoginAttemptService loginAttemptService) {
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
        this.userLogedInCounter = registry.counter("user.login.count");
        this.authenticationManager = authenticationManager;
        this.loginAttemptService = loginAttemptService;

    }


    //3. Trainee username and password matching.
    //4. Trainer username and password matching.
    public String login(AuthDTO dto) {

        String username = dto.getUsername();

        if (loginAttemptService.isBlocked(username)) {
            throw new RuntimeException("User is blocked. Try again later.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            dto.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            List<UserRoleEnum> roles = userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(UserRoleEnum::valueOf)
                    .toList();

            loginAttemptService.loginSucceeded(username);
            userLogedInCounter.increment();
            return jwtTokenService.encode(userDetails.getUsername(), roles);

        } catch (Exception ex) {
            loginAttemptService.loginFailed(username);
            throw new RuntimeException("Invalid username or password");
        }
    }

    public void changePassword(@Valid UserChangePasswordRequestDTO dto) {
        userService.changePassword(dto);
    }

    public void logout(@Valid String token) {
        // add token to blacklist
    }
}
