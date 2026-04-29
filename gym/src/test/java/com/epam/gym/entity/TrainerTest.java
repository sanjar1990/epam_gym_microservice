package com.epam.gym.entity;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class TrainerTest {

    @Test
    void constructor_shouldInitializeTraineesSet() {
        Trainer trainer = new Trainer();

        assertNotNull(trainer.getTrainees());
        assertTrue(trainer.getTrainees().isEmpty());
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        Trainer trainer = new Trainer();
        User user = new User();
        TrainingType trainingType = new TrainingType();

        Long trainingTypeId = 1L;

        trainer.setUser(user);
        trainer.setTrainingTypeId(trainingTypeId);
        trainer.setTrainingType(trainingType);

        assertEquals(user, trainer.getUser());
        assertEquals(trainingTypeId, trainer.getTrainingTypeId());
        assertEquals(trainingType, trainer.getTrainingType());
    }

    @Test
    void traineesRelationship_shouldAddTrainee() {
        Trainer trainer = new Trainer();
        Trainee trainee = new Trainee();

        trainer.setTrainees(new HashSet<>());

        trainer.getTrainees().add(trainee);

        assertTrue(trainer.getTrainees().contains(trainee));
    }

    @Test
    void traineesRelationship_shouldRemoveTrainee() {
        Trainer trainer = new Trainer();
        Trainee trainee = new Trainee();

        trainer.setTrainees(new HashSet<>());
        trainer.getTrainees().add(trainee);

        trainer.getTrainees().remove(trainee);

        assertFalse(trainer.getTrainees().contains(trainee));
    }

    @Test
    void bidirectionalRelationship_shouldBeConsistent_whenManuallyHandled() {
        Trainer trainer = new Trainer();
        Trainee trainee = new Trainee();

        trainer.setTrainees(new HashSet<>());
        trainee.setTrainers(new HashSet<>());

        trainer.getTrainees().add(trainee);
        trainee.getTrainers().add(trainer);

        assertTrue(trainer.getTrainees().contains(trainee));
        assertTrue(trainee.getTrainers().contains(trainer));
    }
}