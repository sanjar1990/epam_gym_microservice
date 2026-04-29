package com.epam.gym.service;

import com.epam.gym.dto.*;
import com.epam.gym.entity.Trainee;
import com.epam.gym.entity.Trainer;
import com.epam.gym.entity.User;
import com.epam.gym.enums.UserRoleEnum;
import com.epam.gym.exceptions.UserNotFoundException;
import com.epam.gym.mapper.trainee.TraineeMapperI;
import com.epam.gym.mapper.trainer.TrainerMapperI;
import com.epam.gym.repository.TraineeRepository;
import com.epam.gym.util.SpringSecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class TraineeService {


    private final TraineeRepository traineeRepository;
    private final UserService userService;
    private final UserRoleService userRoleService;
    private final TrainerService trainerService;
    private final TraineeMapperI traineeMapperI;
    private final TrainerMapperI trainerMapperI;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TraineeService(TraineeRepository traineeRepository, UserService userService,
                          TrainerService trainerService, TraineeMapperI traineeMapperI,
                          TrainerMapperI trainerMapperI, UserRoleService userRoleService,
                          PasswordEncoder passwordEncoder) {
        this.traineeRepository = traineeRepository;
        this.userService = userService;
        this.trainerService = trainerService;
        this.traineeMapperI = traineeMapperI;
        this.trainerMapperI = trainerMapperI;
        this.userRoleService = userRoleService;
        this.passwordEncoder = passwordEncoder;
    }

    //2. Create Trainee profile.
    public AuthDTO createTrainee(CreateTraineeRequestDTO dto) {
        String username = userService.generateUsername(dto.getFirstName(), dto.getLastName());
        String password = userService.generatePassword();

        Trainee trainee = traineeMapperI.toTrainee(dto);
        User user = trainee.getUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setIsActive(true);
        trainee.setUser(user);

        traineeRepository.save(trainee);
        userRoleService.merge(trainee.getUser().getId(), List.of(UserRoleEnum.ROLE_TRAINEE));
        return new AuthDTO(username, password);
    }

    //6. Select Trainee profile by username.

    public TraineeDTO getTraineeByUsername() {
        Trainee trainee = getTrainee(SpringSecurityUtil.getCurrentUser().getUsername());
        return traineeMapperI.toTraineeDTO(trainee);
    }

    //7. Trainee password change
    public void changePassword(UserChangePasswordRequestDTO dto) {
        userService.changePassword(dto);
    }

    //    10. Update trainee profile.
    public TraineeDTO updateTrainee(UpdateTraineeRequestDTO dto) {
        Trainee trainee = getTrainee(SpringSecurityUtil.getCurrentUser().getUsername());
        traineeMapperI.updateTraineeFromDto(dto, trainee);

        log.info("Trainee updated{}", trainee.getId());
        trainee = traineeRepository.save(trainee);
        return traineeMapperI.toTraineeDTO(trainee);
    }

    //    11. Activate/De-activate trainee.
    public void changeStatusTrainee(ChangeStatusRequestDTO dto) {
        log.info("Trainee {} activated/deactivated {}", dto.getIsActive(), dto.getUsername());
        userService.changeStatus(dto);
    }

    //    13. Delete trainee profile by username.
    //  What does boolean return type represent in this case?
//    DONE
    @Transactional
    public void deleteTrainee(String username) {
        Trainee trainee = getTrainee(username);
        for (Trainer trainer : trainee.getTrainers()) {
            trainer.getTrainees().remove(trainee);
        }

        trainee.getTrainers().clear();
        log.info("Trainee deleted{}", trainee.getId());
        traineeRepository.delete(trainee);

    }

    public Trainee getTrainee(String username) {
        return traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found" + username));
    }

    @Transactional
    public List<TrainerDTO> updateTrainerList(String traineeUsername, UpdateTrainersRequestDTO dto) {
        Trainee trainee = getTrainee(traineeUsername);
        List<Trainer> newTrainers = trainerService.getTrainersByUsernames(dto.getTrainerUsernames());

        new HashSet<>(trainee.getTrainers())
                .forEach(trainee::removeTrainer);

        newTrainers.forEach(trainee::addTrainer);

        traineeRepository.save(trainee);
        return trainee.getTrainers().stream().map(trainerMapperI::toTrainerDTO).toList();
    }
}

