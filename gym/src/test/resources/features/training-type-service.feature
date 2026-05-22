Feature: Training Type Service
  @positive
  Scenario: Get all training types successfully
    Given training types exist
    When all training types are requested
    Then training type list should be returned
  @negative
  Scenario: Get empty training type list
    Given no training types exist
    When all training types are requested
    Then empty training type list should be returned