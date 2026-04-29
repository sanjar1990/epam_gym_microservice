package com.epam.gym.config.validation;

import com.epam.gym.config.custom_anotation.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])" +  // at least one lowercase
                    "(?=.*[A-Z])" +         // at least one uppercase
                    "(?=.*\\d)" +           // at least one digit
                    "[A-Za-z\\d@$!%*?&_]{8,}$"; // minimum 8 characters

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.matches(PASSWORD_PATTERN);
    }
}