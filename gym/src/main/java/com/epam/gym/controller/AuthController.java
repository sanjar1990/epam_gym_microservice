package com.epam.gym.controller;

import com.epam.gym.dto.AuthDTO;
import com.epam.gym.dto.UserChangePasswordRequestDTO;
import com.epam.gym.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth Api list", description = "this api is for Authentication")
@RequiredArgsConstructor()
public class AuthController {

    private final AuthService authService;


    //    3. Login (GET method)
    @PostMapping("/login")
    @Operation(summary = "User login", description = "")
    public ResponseEntity<String> login(@Valid @RequestBody AuthDTO dto) {
        log.info("Login auth: {} ", dto.getUsername());
        return ResponseEntity.ok().body(authService.login(dto));
    }

    //    4. Change Password (PUT method)
    @PostMapping("/change-password")
    @Operation(summary = "User Change Password", description = "")
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@Valid @RequestBody UserChangePasswordRequestDTO dto) {
        System.out.println("Login auth: " + dto.getNewPassword());
        authService.changePassword(dto);
    }

    @DeleteMapping("/logout")
    @Operation(summary = "User logout", description = "")
    @ResponseStatus(HttpStatus.OK)
    public void logout(@Valid String token) {
        authService.logout(token);
    }
}
