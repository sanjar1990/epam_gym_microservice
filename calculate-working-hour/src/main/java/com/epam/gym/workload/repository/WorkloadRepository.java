package com.epam.gym.workload.repository;

import com.epam.gym.workload.dto.MonthlySummaryDTO;
import com.epam.gym.workload.entity.Workload;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkloadRepository extends MongoRepository<Workload, Long> {
    Optional<Workload> findByTrainerUsername(String username);

    @Aggregation(pipeline = {

            "{ $match: { trainerUsername: ?0 } }",

            "{ $unwind: '$years' }",

            "{ $unwind: '$years.months' }",

            "{ $project: { " +
                    "_id: 0, " +
                    "trainerUsername: '$trainerUsername', " +
                    "firstName: '$trainerFirstName', " +
                    "lastName: '$trainerLastName', " +
                    "year: '$years.year', " +
                    "month: '$years.months.month', " +
                    "totalDuration: '$years.months.trainingSummaryDuration' " +
                    "} }"
    })
    List<MonthlySummaryDTO> getMonthlySummary(String username);


    @Query(value = "{ 'trainingDate': ?0, 'trainingDuration': ?1 }", delete = true)
    void deleteWorkloads(LocalDate trainingDate, int trainingDuration);
}
