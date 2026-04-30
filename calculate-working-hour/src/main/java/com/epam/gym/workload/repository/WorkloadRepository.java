package com.epam.gym.workload.repository;

import com.epam.gym.workload.entity.Workload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkloadRepository extends JpaRepository<Workload, Long> {
    // TODO:
    //  After you align your model with the task you'll probably won't need this query, but for the future in such cases
    //  consider using Projections - they are more type-safe and easier to maintain than Object[] arrays
    @Query("""
                SELECT 
                    w.trainerUsername,
                    w.firstName,
                    w.lastName,
                    YEAR(w.trainingDate),
                    MONTH(w.trainingDate),
                    SUM(w.trainingDuration)
                FROM Workload w
                WHERE w.trainerUsername = :username
                GROUP BY 
                    w.trainerUsername, w.firstName, w.lastName, 
                    YEAR(w.trainingDate), MONTH(w.trainingDate)
            """)
    List<Object[]> getMonthlySummary(String username);


    @Transactional
    @Modifying
    @Query("DELETE FROM Workload w WHERE w.trainingDate = :trainingDate and w.trainingDuration=:trainingDuration")
    void deleteWorkloads(LocalDate trainingDate, int trainingDuration);
}
