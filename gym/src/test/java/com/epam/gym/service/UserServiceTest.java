package com.epam.gym.service;

import com.epam.gym.dto.ChangeStatusRequestDTO;
import com.epam.gym.dto.UserChangePasswordRequestDTO;
import com.epam.gym.entity.User;
import com.epam.gym.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userService, "CHARS", "ABC123");
    }


    @Test
    void generateUsername_shouldReturnSimple_whenNoDuplicates() {
        when(userRepository.countAllByUsername("john.doe")).thenReturn(0);

        String result = userService.generateUsername("john", "doe");

        assertEquals("john.doe", result);
    }

    @Test
    void generateUsername_shouldAppendCount_whenDuplicateExists() {
        when(userRepository.countAllByUsername("john.doe")).thenReturn(2);

        String result = userService.generateUsername("john", "doe");

        assertEquals("john.doe2", result);
    }


    @Test
    void generatePassword_shouldGenerateValidPassword() {
        String password = userService.generatePassword();

        assertNotNull(password);
        assertEquals(10, password.length());

        for (char c : password.toCharArray()) {
            assertTrue("ABC123".contains(String.valueOf(c)));
        }
    }

    @Test
    void generatePassword_shouldThrow_whenCharsInvalid() {
        ReflectionTestUtils.setField(userService, "CHARS", "");

        assertThrows(Exception.class, () -> userService.generatePassword());
    }


    @Test
    void changeStatus_shouldUpdateAndSaveUser() {
        User user = new User();
        user.setUsername("john");
        user.setIsActive(true);

        ChangeStatusRequestDTO dto = new ChangeStatusRequestDTO();
        dto.setUsername("john");
        dto.setIsActive(false);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        userService.changeStatus(dto);

        assertFalse(user.getIsActive());
        verify(userRepository).save(user);
    }

    @Test
    void changeStatus_shouldThrow_whenUserNotFound() {
        ChangeStatusRequestDTO dto = new ChangeStatusRequestDTO();
        dto.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.changeStatus(dto));
    }


    @Test
    void changePassword_shouldUpdatePassword_whenValid() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("encodedOld");

        UserChangePasswordRequestDTO dto = new UserChangePasswordRequestDTO();
        dto.setUsername("john");
        dto.setOldPassword("old");
        dto.setNewPassword("new123");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "encodedOld")).thenReturn(true);
        when(passwordEncoder.encode("new123")).thenReturn("encodedNew");

        userService.changePassword(dto);

        assertEquals("encodedNew", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_shouldThrow_whenUserNotFound() {
        UserChangePasswordRequestDTO dto = new UserChangePasswordRequestDTO();
        dto.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.changePassword(dto));
    }

    @Test
    void changePassword_shouldThrow_whenOldPasswordInvalid() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("encodedOld");

        UserChangePasswordRequestDTO dto = new UserChangePasswordRequestDTO();
        dto.setUsername("john");
        dto.setOldPassword("wrong");
        dto.setNewPassword("new123");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedOld")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.changePassword(dto));
        verify(userRepository, never()).save(any());
    }


    @Test
    void getUser_shouldReturnUser_whenExists() {
        User user = new User();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        User result = userService.getUser("john");

        assertNotNull(result);
    }

    @Test
    void getUser_shouldThrow_whenNotFound() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUser("john"));
    }
}