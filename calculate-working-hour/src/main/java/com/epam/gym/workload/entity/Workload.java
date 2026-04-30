package com.epam.gym.workload.entity;

import com.epam.gym.workload.enums.ActionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


// TODO:
//  This is a flat model, compared to the nested one specified in the task . It's not only denormalizes database
//  with one row per trainer per month and overcomplicates grouping and summing up the data in your service, but also
//  will have to be changed for the future NoSQL task. Please update according to the requirements
@Entity
@Getter
@Setter
public class Workload extends BaseEntity {
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private Boolean isActive;
    @Column(nullable = false)
    private LocalDate trainingDate;
    @Column(nullable = false)
    private Integer trainingDuration;
    @Column()
    @Enumerated(EnumType.STRING)
    private ActionType actionType;
    @Column(nullable = false)
    private Long trainingId;

}
