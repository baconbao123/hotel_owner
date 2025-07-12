package com.hotel.webapp.validation;

import com.hotel.webapp.validation.validator.MultipartFileCheckEmptyAndSizeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MultipartFileCheckEmptyAndSizeValidator.class)
public @interface MultipartFileCheckEmptyAndSize {
  String message() default "_NOT_EMPTY";

  String field();

  int value() default Integer.MAX_VALUE;

  boolean force() default true;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}