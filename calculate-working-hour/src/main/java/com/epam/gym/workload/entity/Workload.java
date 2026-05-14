package com.epam.gym.workload.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Document(collection = "workload")
@CompoundIndex(
        name = "trainer_username_idx",
        def = "{'trainerUsername': 1}",
        unique = true
)
public class Workload {

    @Id
    private String id;

    private String trainerUsername;

    private String firstName;

    private String lastName;

    private Boolean status;

    private List<YearSummary> years;

    private LocalDateTime createdOn = LocalDateTime.now();

    private LocalDateTime updatedOn = LocalDateTime.now();
}
