Feature: Training Service

  Scenario: Add training successfully
    Given a valid create training request
    When training is added
    Then training id should be returned
    And training should be saved
    And workload service should be updated

  Scenario: Add training with invalid training type
    Given a create training request with invalid training type
    When training is added
    Then training type mismatch exception should be thrown

  Scenario: Get trainings by trainee criteria
    Given trainings exist for trainee criteria
    When trainings are requested by trainee criteria
    Then trainee trainings should be returned

  Scenario: Get trainings by trainer criteria
    Given trainings exist for trainer criteria
    When trainings are requested by trainer criteria
    Then trainer trainings should be returned

  Scenario: Delete training successfully
    Given an existing training
    When training is deleted
    Then training should be removed
    And workload delete action should be triggered

  Scenario: Delete training that does not exist
    Given training does not exist
    When training is deleted
    Then training not found exception should be thrown