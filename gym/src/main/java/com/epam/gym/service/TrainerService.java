package com.epam.gym.service;

import com.epam.gym.dto.*;
import com.epam.gym.entity.Trainer;
import com.epam.gym.entity.User;
import com.epam.gym.enums.UserRoleEnum;
import com.epam.gym.exceptions.UserNotFoundException;
import com.epam.gym.mapper.trainer.TrainerMapperI;
import com.epam.gym.repository.TrainerRepository;
import com.epam.gym.util.SpringSecurityUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@Service
@Slf4j
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final UserService userService;
    private final TrainerMapperI trainerMapperI;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public TrainerService(TrainerRepository trainerRepository, UserService userService,
                          TrainerMapperI trainerMapperI, UserRoleService userRoleService,
                          PasswordEncoder passwordEncoder) {
        this.trainerRepository = trainerRepository;
        this.userService = userService;
        this.trainerMapperI = trainerMapperI;
        this.userRoleService = userRoleService;
        this.passwordEncoder = passwordEncoder;
    }

    //1. Create Trainer profile.
    public AuthDTO createTrainer(CreateTrainerRequestDTO dto) {
        String username = userService.generateUsername(dto.getFirstName(), dto.getLastName());
        String password = userService.generatePassword();

        Trainer trainer = trainerMapperI.toEntity(dto);
        User user = trainer.getUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setIsActive(true);

        trainerRepository.save(trainer);
        userRoleService.merge(trainer.getUser().getId(), List.of(UserRoleEnum.ROLE_TRAINER, UserRoleEnum.ROLE_ADMIN));
        log.info("Trainer created: {}", trainer.getId());
        return new AuthDTO(username, password);
    }

    //5. Select Trainer profile by username.
    public TrainerDTO getTrainerByUsername() {
        Trainer trainer = getTrainerEntityByUsername(SpringSecurityUtil.getCurrentUser().getUsername());
        return trainerMapperI.toTrainerDTO(trainer);
    }

    //8. Trainer password change
    public void changePassword(UserChangePasswordRequestDTO dto) {
        log.info("Changing password for user: {}", dto.getUsername());
        userService.changePassword(dto);
    }

    //9. Update trainer profile.
    public TrainerDTO updateTrainer(UpdateTrainerRequestDTO dto) {
        Trainer trainer = getTrainerEntityByUsername(SpringSecurityUtil.getCurrentUser().getUsername());
        trainerMapperI.updateTrainerFromDto(dto, trainer);
        trainerRepository.save(trainer);
        log.info("Trainer updated: {}", trainer.getId());
        return trainerMapperI.toTrainerDTO(trainer);
    }

    //12. Activate/De-activate trainer.
    public void changeStatusTrainee(ChangeStatusRequestDTO dto) {
        log.info("Changing status for user: {}", dto.getUsername());
        userService.changeStatus(dto);
    }

    //    17. Get trainers list that not assigned on trainee by trainee's username.
    public List<TrainerDTO> getTrainersNotAssignedOnTrainee(String traineeUsername) {
        return trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername)
                .stream().map(trainerMapperI::toTrainerDTO).toList();
    }

    public Trainer getTrainerEntityByUsername(String username) {
        return trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public List<Trainer> getTrainersByUsernames(List<String> trainerUsernames) {
        return trainerRepository.findByUserUsernameIn(trainerUsernames);
    }
}
