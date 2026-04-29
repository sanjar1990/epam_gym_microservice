package com.epam.gym.repository;

import com.epam.gym.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    Optional<Trainer> findByUserUsername(String username);

    @Query("""
            SELECT t
            FROM Trainer t
            WHERE t.id NOT IN (
                SELECT tr.id
                FROM Trainer tr
                JOIN tr.trainees trainee
                WHERE trainee.user.username = :username
            )
            """)
    List<Trainer> findTrainersNotAssignedToTrainee(String username);

    List<Trainer> findByUserUsernameIn(Collection<String> userUsernames);
}



