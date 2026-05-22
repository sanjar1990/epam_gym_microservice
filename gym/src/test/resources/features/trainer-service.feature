Feature: Trainer Service

  Scenario: Create trainer successfully
    Given a valid create trainer request
    When the trainer profile is created
    Then trainer credentials should be returned
    And trainer should be saved

  Scenario: Get trainer by username successfully
    Given an existing trainer
    When trainer profile is requested
    Then trainer details should be returned

  Scenario: Update trainer successfully
    Given an existing trainer for update
    When trainer profile is updated
    Then updated trainer should be returned

  Scenario: Change trainer password
    Given a valid trainer password change request
    When trainer password is changed
    Then trainer password should be updated

  Scenario: Change trainer status
    Given a valid trainer status request
    When trainer status is changed
    Then trainer status should be updated

  Scenario: Get trainers not assigned to trainee
    Given trainers not assigned to trainee
    When trainers not assigned are requested
    Then unassigned trainers should be returned

  Scenario: Get trainer that does not exist
    Given trainer username does not exist
    When trainer profile is requested
    Then trainer not found exception should be thrown