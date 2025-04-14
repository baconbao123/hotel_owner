package com.hotel.webapp.validation.validator;

import com.hotel.webapp.validation.NullableAndCheckRegexp;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.regex.Pattern;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class NullableAndCheckRegexpValidator implements ConstraintValidator<NullableAndCheckRegexp, String> {
  String regexp;
  String field;

  @Override
  public void initialize(NullableAndCheckRegexp constraintAnnotation) {
    this.field = constraintAnnotation.field();
    this.regexp = constraintAnnotation.regex();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
      return true;
    }
    boolean isValid = Pattern.matches(regexp, value);
    if (!isValid) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(field + "_INVALID_REGEX")
             .addConstraintViolation();
    }
    return isValid;
  }
}
