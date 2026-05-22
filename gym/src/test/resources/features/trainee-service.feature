Feature: Trainee Service

  Scenario: Create trainee successfully
    Given a valid create trainee request
    When the trainee profile is created
    Then trainee credentials should be returned
    And trainee should be saved

  Scenario: Get trainee by username successfully
    Given an existing trainee
    When trainee profile is requested
    Then trainee details should be returned

  Scenario: Update trainee successfully
    Given an existing trainee for update
    When trainee profile is updated
    Then updated trainee should be returned

  Scenario: Delete trainee successfully
    Given an existing trainee with trainers and trainings
    When trainee profile is deleted
    Then trainee should be removed

  Scenario: Get trainee that does not exist
    Given trainee username does not exist
    When trainee profile is requested
    Then trainee not found exception should be thrown

  Scenario: Change trainee status
    Given a valid change status request
    When trainee status is changed
    Then trainee status should be updated

  Scenario: Change trainee password
    Given a valid password change request
    When trainee password is changed
    Then password should be updated

  Scenario: Update trainer list successfully
    Given an existing trainee and trainers
    When trainee trainer list is updated
    Then updated trainer list should be returned