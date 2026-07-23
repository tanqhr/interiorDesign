package com.taico.interiorDesign.error.validation.validator;


import com.taico.interiorDesign.error.annotation.UniqueEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.taico.interiorDesign.service.UserService;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserService userService;

    public UniqueEmailValidator (UserService userService) {

        this.userService = userService;
    }

    @Override
    public void initialize (UniqueEmail constraintAnnotation) {

        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid (String email, ConstraintValidatorContext context) {

        return !this.userService.existsByEmail(email);
    }
}
