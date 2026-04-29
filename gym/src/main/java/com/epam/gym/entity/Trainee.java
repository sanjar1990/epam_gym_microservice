package com.epam.gym.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Trainee extends BaseEntity {
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    @Column(columnDefinition = "TEXT")
    private String address;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    @OneToMany(mappedBy = "trainee",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Training> trainings;
    @ManyToMany(mappedBy = "trainees")
    private Set<Trainer> trainers = new HashSet<>();


    public void addTrainer(Trainer trainer) {
        this.trainers.add(trainer);
        trainer.getTrainees().add(this);
    }

    public void removeTrainer(Trainer trainer) {
        this.trainers.remove(trainer);
        trainer.getTrainees().remove(this);
    }
}
