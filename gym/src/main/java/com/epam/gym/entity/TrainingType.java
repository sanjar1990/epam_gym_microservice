package com.epam.gym.entity;

import com.epam.gym.enums.TrainingTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "training_type")
public class TrainingType extends BaseEntity {
    @Column()
    @Enumerated(EnumType.STRING)
    private TrainingTypeEnum trainingTypeName;
}

