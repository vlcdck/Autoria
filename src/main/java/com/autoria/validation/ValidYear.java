package com.autoria.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = YearValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidYear {
    String message() default "Year must not be in the future and not earlier than 1886";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}