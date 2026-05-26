package com.epam.gym.component.steps;

import com.epam.gym.dto.ChangeStatusRequestDTO;
import com.epam.gym.dto.UserChangePasswordRequestDTO;
import com.epam.gym.entity.User;
import com.epam.gym.repository.UserRepository;
import com.epam.gym.service.UserService;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceStepDefinition {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    private String firstName;
    private String lastName;

    private String generatedUsername;
    private String generatedPassword;

    private ChangeStatusRequestDTO changeStatusRequestDTO;
    private UserChangePasswordRequestDTO passwordRequestDTO;

    private User user;

    private Exception exception;

    @Before
    public void setup() {

        MockitoAnnotations.openMocks(this);

        userService = new UserService(
                userRepository,
                passwordEncoder
        );

        ReflectionTestUtils.setField(
                userService,
                "CHARS",
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        );
    }

    @Given("first name is {string} and last name is {string}")
    public void first_name_and_last_name(String first, String last) {

        this.firstName = first;
        this.lastName = last;
    }

    @Given("username does not exist")
    public void username_does_not_exist() {

        when(userRepository.countAllByUsername("John.Doe"))
                .thenReturn(0);
    }

    @Given("username already exists 2 times")
    public void username_already_exists_2_times() {

        when(userRepository.countAllByUsername("John.Doe"))
                .thenReturn(2);
    }

    @When("username is generated")
    public void username_is_generated() {

        generatedUsername =
                userService.generateUsername(firstName, lastName);
    }

    @Then("generated username should be {string}")
    public void generated_username_should_be(String expected) {

        Assertions.assertEquals(expected, generatedUsername);
    }

    @Given("password characters are configured")
    public void password_characters_are_configured() {
    }

    @When("password is generated")
    public void password_is_generated() {

        generatedPassword = userService.generatePassword();
    }

    @Then("password should have length {int}")
    public void password_should_have_length(Integer length) {

        Assertions.assertEquals(length, generatedPassword.length());
    }

    @Given("existing user with username {string}")
    public void existing_user_with_username(String username) {

        user = new User();
        user.setUsername(username);
        user.setIsActive(true);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        changeStatusRequestDTO = new ChangeStatusRequestDTO();
        changeStatusRequestDTO.setUsername(username);
    }

    @When("user status is changed to false")
    public void user_status_is_changed_to_false() {

        changeStatusRequestDTO.setIsActive(false);

        userService.changeStatus(changeStatusRequestDTO);
    }

    @Then("user active status should be false")
    public void user_active_status_should_be_false() {

        Assertions.assertFalse(user.getIsActive());
    }

    @Then("user should be saved")
    public void user_should_be_saved() {

        verify(userRepository, times(1))
                .save(user);
    }

    @Given("existing user for password change")
    public void existing_user_for_password_change() {

        user = new User();
        user.setUsername("john");
        user.setPassword("encodedOldPassword");

        passwordRequestDTO = new UserChangePasswordRequestDTO();
        passwordRequestDTO.setUsername("john");
        passwordRequestDTO.setOldPassword("oldPassword");
        passwordRequestDTO.setNewPassword("newPassword");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));
    }

    @Given("old password is valid")
    public void old_password_is_valid() {

        when(passwordEncoder.matches(
                "oldPassword",
                "encodedOldPassword"
        )).thenReturn(true);

        when(passwordEncoder.encode("newPassword"))
                .thenReturn("encodedNewPassword");
    }

    @Given("old password is invalid")
    public void old_password_is_invalid() {

        when(passwordEncoder.matches(
                "oldPassword",
                "encodedOldPassword"
        )).thenReturn(false);
    }

    @When("password is changed")
    public void password_is_changed() {

        try {
            userService.changePassword(passwordRequestDTO);
        } catch (Exception ex) {
            exception = ex;
        }
    }

    @Then("encoded password should be saved")
    public void encoded_password_should_be_saved() {

        Assertions.assertEquals(
                "encodedNewPassword",
                user.getPassword()
        );

        verify(userRepository, times(1))
                .save(user);
    }

    @Then("invalid old password exception should be thrown")
    public void invalid_old_password_exception_should_be_thrown() {

        Assertions.assertNotNull(exception);

        Assertions.assertEquals(
                "Invalid old password",
                exception.getMessage()
        );
    }

    @Given("user does not exist")
    public void user_does_not_exist() {

        passwordRequestDTO = new UserChangePasswordRequestDTO();
        passwordRequestDTO.setUsername("john");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());
    }

    @Then("user not found exception should be thrown")
    public void user_not_found_exception_should_be_thrown() {

        Assertions.assertNotNull(exception);

        Assertions.assertEquals(
                "User not found",
                exception.getMessage()
        );
    }
}