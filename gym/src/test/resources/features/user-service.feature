Feature: User Service

  Scenario: Generate username successfully
    Given first name is "John" and last name is "Doe"
    And username does not exist
    When username is generated
    Then generated username should be "John.Doe"

  Scenario: Generate username with duplicate
    Given first name is "John" and last name is "Doe"
    And username already exists 2 times
    When username is generated
    Then generated username should be "John.Doe2"

  Scenario: Generate random password
    Given password characters are configured
    When password is generated
    Then password should have length 10

  Scenario: Change user status successfully
    Given existing user with username "john"
    When user status is changed to false
    Then user active status should be false
    And user should be saved

  Scenario: Change password successfully
    Given existing user for password change
    And old password is valid
    When password is changed
    Then encoded password should be saved

  Scenario: Change password with invalid old password
    Given existing user for password change
    And old password is invalid
    When password is changed
    Then invalid old password exception should be thrown

  Scenario: Change password when user not found
    Given user does not exist
    When password is changed
    Then user not found exception should be thrown