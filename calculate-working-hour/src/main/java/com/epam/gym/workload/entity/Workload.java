package com.epam.gym.workload.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Document(collection = "workload")
@CompoundIndexes({

        @CompoundIndex(
                name = "trainer_username_idx",
                def = "{'trainerUsername': 1}",
                unique = true
        ),

        @CompoundIndex(
                name = "trainer_name_idx",
                def = "{'firstName': 1, 'lastName': 1}"
        )
})
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
