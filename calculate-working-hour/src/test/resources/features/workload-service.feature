Feature: Workload Service

  Scenario: Add workload for new trainer
    Given valid add workload request
    And workload does not exist
    When workload update is performed
    Then workload should be saved
    And workload duration should be increased

  Scenario: Add workload to existing month
    Given existing workload with month summary
    When workload update is performed
    Then workload duration should be increased

  Scenario: Delete workload successfully
    Given existing workload for delete
    When delete workload update is performed
    Then workload duration should be decreased

  Scenario: Delete workload and remove month
    Given existing workload with exact duration
    When delete workload update is performed
    Then month summary should be removed

  Scenario: Delete workload and remove year
    Given existing workload with single month
    When delete workload update is performed
    Then year summary should be removed

  Scenario: Workload request is null
    Given null workload request
    When workload update is performed
    Then null request exception should be thrown

  Scenario: Delete workload but trainer workload not found
    Given delete request with missing workload
    When delete workload update is performed
    Then workload not found exception should be thrown

  Scenario: Get workload summary successfully
    Given monthly summary exists
    When workload summary is requested
    Then workload summary response should be returned

  Scenario: Get workload summary with empty rows
    Given no workload summary exists
    When workload summary is requested
    Then workload summary response should be null

