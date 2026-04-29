package com.epam.gym.workload.repository;

import com.epam.gym.workload.entity.Workload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkloadRepository extends JpaRepository<Workload, Long> {
    @Query("""
                SELECT 
                    w.username,
                    w.firstName,
                    w.lastName,
                    w.isActive,
                    YEAR(w.trainingDate),
                    MONTH(w.trainingDate),
                    SUM(w.trainingDuration)
                FROM Workload w
                WHERE w.username = :username and w.isActive = true
                GROUP BY 
                    w.username, w.firstName, w.lastName, w.isActive,
                    YEAR(w.trainingDate), MONTH(w.trainingDate)
            """)
    List<Object[]> getMonthlySummary(String username);


    Optional<Workload> findByTrainingId(Long trainingId);

    @Transactional
    @Modifying
    @Query("delete from Workload w where w.trainingId in :trainingIds")
    void deleteByTrainingId(List<Long> trainingIds);
}
