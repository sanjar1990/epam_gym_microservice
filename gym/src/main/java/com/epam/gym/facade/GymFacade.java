package com.epam.gym.facade;

import com.epam.gym.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GymFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final AuthService authService;
    private final UserService userService;
    private final TrainingTypeService trainingTypeService;


    @Autowired
    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService,
                     AuthService authService,
                     UserService userService,
                     TrainingTypeService trainingTypeService) {

        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.authService = authService;
        this.userService = userService;
        this.trainingTypeService = trainingTypeService;
    }
}
