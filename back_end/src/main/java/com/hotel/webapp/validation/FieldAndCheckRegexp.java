package com.hotel.webapp.validation;

import com.hotel.webapp.validation.validator.FieldAndCheckRegexpValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = {FieldAndCheckRegexpValidator.class})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldAndCheckRegexp {
  String message() default "";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String field();

  ForceType force() default ForceType.OPTIONAL;

  String regex();

  String notice();
}
