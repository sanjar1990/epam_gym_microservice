package com.epam.gym.specification;

import com.epam.gym.dto.GetTraineeTrainingsCriteriaFilterDTO;
import com.epam.gym.dto.GetTrainerTrainingsCriteriaFilterDTO;
import com.epam.gym.entity.Training;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

//  Good job on using Criteria API! Just a couple of questions:
//  1. Is there any specific reason to concatenate first&last names instead of using 'username' field directly?
//  14. Get Trainee Trainings List by trainee username and criteria (from date, to date, trainer
//  name, training type). this method is requiring Trainer name or Trainee name.
//  I don't understand should I search by firstName or lastName or username.
//  in Username can be serial number if User's lastName and firstName are same.
//  I changed it to username?
//  2. Are both criteria for trainee and trainer same? Please double check the task on which fields are used in each case.
//  3. For those fields which are used in both criteria, consider refactoring into a separate method to avoid code duplication.
//  I removed the code duplication.
//DONE
public class TrainingSpecification {

    public static Specification<Training> filterByCriteriaForTrainee(GetTraineeTrainingsCriteriaFilterDTO dto) {
        return (root, query, cb) -> {

            Predicate predicate = cb.conjunction();

            Join<Object, Object> traineeJoin = root.join("trainee");
            Join<Object, Object> traineeUserJoin = traineeJoin.join("user");

            predicate = cb.and(predicate,
                    cb.equal(traineeUserJoin.get("username"), dto.getTraineeUsername())
            );

            predicate = addDateFilters(predicate, root, cb, dto.getFromDate(), dto.getToDate());
            predicate = addTrainingTypeFilter(predicate, root, cb, dto.getTrainingType());
            predicate = addTrainerNameFilter(predicate, root, cb, dto.getTrainerName());

            return predicate;
        };
    }

    public static Specification<Training> filterByCriteriaForTrainer(GetTrainerTrainingsCriteriaFilterDTO dto) {
        return (root, query, cb) -> {

            Predicate predicate = cb.conjunction();

            Join<Object, Object> trainerJoin = root.join("trainer");
            Join<Object, Object> trainerUserJoin = trainerJoin.join("user");

            predicate = cb.and(predicate,
                    cb.equal(trainerUserJoin.get("username"), dto.getTrainerUsername())
            );

            predicate = addDateFilters(predicate, root, cb, dto.getFromDate(), dto.getToDate());
            predicate = addTrainingTypeFilter(predicate, root, cb, dto.getTrainingType());
            predicate = addTraineeNameFilter(predicate, root, cb, dto.getTraineeName());

            return predicate;
        };
    }

    private static Predicate addDateFilters(
            Predicate predicate,
            Root<Training> root,
            CriteriaBuilder cb,
            LocalDate fromDate,
            LocalDate toDate) {

        if (fromDate != null) {
            predicate = cb.and(predicate,
                    cb.greaterThanOrEqualTo(root.get("trainingDate"), fromDate));
        }

        if (toDate != null) {
            predicate = cb.and(predicate,
                    cb.lessThanOrEqualTo(root.get("trainingDate"), toDate));
        }

        return predicate;
    }

    private static Predicate addTrainingTypeFilter(
            Predicate predicate,
            Root<Training> root,
            CriteriaBuilder cb,
            String trainingType) {

        if (trainingType != null && !trainingType.isBlank()) {

            Join<Object, Object> typeJoin = root.join("trainingType");

            predicate = cb.and(predicate,
                    cb.equal(typeJoin.get("trainingTypeName"), trainingType));
        }

        return predicate;
    }

    private static Predicate addTrainerNameFilter(
            Predicate predicate,
            Root<Training> root,
            CriteriaBuilder cb,
            String trainerUsername) {

        if (trainerUsername != null && !trainerUsername.isBlank()) {

            Join<Object, Object> trainerJoin = root.join("trainer");
            Join<Object, Object> trainerUserJoin = trainerJoin.join("user");


            predicate = cb.and(predicate,
                    cb.like(cb.lower(trainerUserJoin.get("username")), "%" + trainerUsername.toLowerCase() + "%"));
        }

        return predicate;
    }

    private static Predicate addTraineeNameFilter(
            Predicate predicate,
            Root<Training> root,
            CriteriaBuilder cb,
            String traineeUsername) {

        if (traineeUsername != null && !traineeUsername.isBlank()) {

            Join<Object, Object> traineeJoin = root.join("trainee");
            Join<Object, Object> traineeUserJoin = traineeJoin.join("user");

            predicate = cb.and(predicate,
                    cb.like(cb.lower(traineeUserJoin.get("username")), "%" + traineeUsername.toLowerCase() + "%"));
        }
        return predicate;
    }
}