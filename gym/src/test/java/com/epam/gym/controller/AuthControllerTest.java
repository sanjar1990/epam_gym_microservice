package com.epam.gym.controller;

import com.epam.gym.config.security.CustomUserDetailsService;
import com.epam.gym.dto.AuthDTO;
import com.epam.gym.dto.UserChangePasswordRequestDTO;
import com.epam.gym.service.AuthService;
import com.epam.gym.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void login_shouldReturn200_andCallService() throws Exception {

        AuthDTO dto = new AuthDTO();
        dto.setUsername("john");
        dto.setPassword("Toshiba_1990$"); // FIXED

        when(authService.login(any())).thenReturn("token123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("token123"));

        verify(authService).login(any(AuthDTO.class));
    }

    @Test
    void login_shouldReturn400_whenInvalidRequest() throws Exception {

        AuthDTO dto = new AuthDTO(); // invalid

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changePassword_shouldReturn200_andCallService() throws Exception {

        UserChangePasswordRequestDTO dto = new UserChangePasswordRequestDTO();
        dto.setUsername("john");
        dto.setOldPassword("old");
        dto.setNewPassword("New12sdsd_3@");

        doNothing().when(authService).changePassword(any());

        mockMvc.perform(post("/api/v1/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(authService).changePassword(any(UserChangePasswordRequestDTO.class));
    }

    @Test
    void changePassword_shouldReturn400_whenInvalidRequest() throws Exception {

        UserChangePasswordRequestDTO dto = new UserChangePasswordRequestDTO();

        mockMvc.perform(post("/api/v1/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}