package com.epam.gym.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class TraineeTest {

    @Test
    void addTrainer_shouldAddBothSides() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();

        trainee.setTrainers(new HashSet<>());
        trainer.setTrainees(new HashSet<>());

        trainee.addTrainer(trainer);

        assertTrue(trainee.getTrainers().contains(trainer));
        assertTrue(trainer.getTrainees().contains(trainee));
    }

    @Test
    void removeTrainer_shouldRemoveBothSides() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();

        trainee.setTrainers(new HashSet<>());
        trainer.setTrainees(new HashSet<>());

        trainee.addTrainer(trainer);

        trainee.removeTrainer(trainer);

        assertFalse(trainee.getTrainers().contains(trainer));
        assertFalse(trainer.getTrainees().contains(trainee));
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        Trainee trainee = new Trainee();
        User user = new User();

        LocalDate dob = LocalDate.of(2000, 1, 1);
        String address = "Seoul";

        trainee.setDateOfBirth(dob);
        trainee.setAddress(address);
        trainee.setUser(user);

        assertEquals(dob, trainee.getDateOfBirth());
        assertEquals(address, trainee.getAddress());
        assertEquals(user, trainee.getUser());
    }
}