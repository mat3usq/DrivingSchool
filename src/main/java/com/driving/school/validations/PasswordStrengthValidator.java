package com.driving.school.validations;

import com.driving.school.annotations.PasswordValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordStrengthValidator implements ConstraintValidator<PasswordValidator, String> {
    @Override
    public boolean isValid(String passwordField, ConstraintValidatorContext cxt) {
        if (passwordField == null)
            return false;

        boolean hasUpperCase = !passwordField.equals(passwordField.toLowerCase());
        boolean hasLowerCase = !passwordField.equals(passwordField.toUpperCase());
        boolean hasDigit = passwordField.matches(".*\\d.*");
        boolean hasSpecialChar = passwordField.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }
}
