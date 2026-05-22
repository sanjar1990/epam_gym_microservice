Feature: Authentication Service

  Scenario: Successful login
    Given a valid authentication request
    When the user logs in
    Then a JWT token should be returned
    And login success should be recorded

  Scenario: Login fails because user is blocked
    Given a blocked user authentication request
    When the user tries to log in
    Then an exception with message "User is blocked. Try again later." should be thrown

  Scenario: Login fails because credentials are invalid
    Given an invalid authentication request
    When the user tries to log in
    Then an exception with message "Invalid username or password" should be thrown
    And login failure should be recorded

  Scenario: Change password successfully
    Given a valid change password request
    When the password is changed
    Then the password should be updated