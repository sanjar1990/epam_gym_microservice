package com.epam.gym.service;

import com.epam.gym.dto.ChangeStatusRequestDTO;
import com.epam.gym.dto.UserChangePasswordRequestDTO;
import com.epam.gym.entity.User;
import com.epam.gym.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class UserService {
    @Value("${password.characters}")
    private String CHARS;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired()
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String generateUsername(String firstName, String lastName) {
        String username = firstName + "." + lastName;

        //  You have defined an unique constraint on username field in User entity which is good.
        //  Now you generate the username filtering only active users and later call repo.save()
        //  What happens if user with the same first and last name already exists but in status active=false?
        //  It will throw exception. I removed Status check.
//        Done
        int count = userRepository.countAllByUsername(username);
        return count == 0 ? username : username + count;
    }


    public String generatePassword() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }

        return sb.toString();
    }


    public void changeStatus(ChangeStatusRequestDTO dto) {
        User user = getUser(dto.getUsername());
        user.setIsActive(dto.getIsActive());
        userRepository.save(user);
    }

    //7. Trainee password change
    public void changePassword(UserChangePasswordRequestDTO dto) {
        User user = userRepository.findByUsername(
                        dto.getUsername())
                .orElseThrow(() -> {
                    log.error("User not found {}", dto.getUsername());
                    return new RuntimeException("User not found");

                });
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            log.error("Invalid old password");
            throw new RuntimeException("Invalid old password");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found")
        );
    }

}

