package com.hotel.webapp.validation;

import com.hotel.webapp.validation.validator.NullableAndCheckRegexpValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = {NullableAndCheckRegexpValidator.class})
@Retention(RetentionPolicy.RUNTIME)
public @interface NullableAndCheckRegexp {
  String message() default "{filed} Invalid}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  String field();

  String regex();
}
