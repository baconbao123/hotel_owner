package com.hotel.webapp.validation.validator;

import com.hotel.webapp.validation.FieldAndCheckRegexp;
import com.hotel.webapp.validation.ForceType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.regex.Pattern;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class FieldAndCheckRegexpValidator implements ConstraintValidator<FieldAndCheckRegexp, String> {
  String regexp;
  String field;
  ForceType force;
  String notice;

  @Override
  public void initialize(FieldAndCheckRegexp constraintAnnotation) {
    this.field = constraintAnnotation.field();
    this.force = constraintAnnotation.force();
    this.regexp = constraintAnnotation.regex();
    this.notice = constraintAnnotation.notice();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
      if (force == ForceType.MANDATORY) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(field + "_NOT_EMPTY")
               .addConstraintViolation();
        return false;
      }

      return true;
    }

    boolean isValid = Pattern.matches(regexp, value);

    if (!isValid) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(notice + "_NOTICE")
             .addConstraintViolation();
    }

    return isValid;
  }
}
