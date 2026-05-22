Feature: Workload JMS Listener

  Scenario: Successfully process workload message
    Given valid workload JMS message
    And valid jwt token
    When workload message is consumed
    Then workload service should be invoked

  Scenario: Invalid token while processing workload message
    Given valid workload JMS message
    And invalid jwt token
    When workload message is consumed
    Then security exception should be thrown

  Scenario: Workload service throws exception
    Given valid workload JMS message
    And valid jwt token
    And workload service failure
    When workload message is consumed
    Then runtime exception should be thrown