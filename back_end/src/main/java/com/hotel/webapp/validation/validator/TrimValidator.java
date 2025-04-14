package com.hotel.webapp.validation.validator;

import com.hotel.webapp.validation.Trim;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TrimValidator implements ConstraintValidator<Trim, String> {
  @Override
  public void initialize(Trim constraintAnnotation) {

  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
      return true; // Allow null or isEmpty
    }
    return value.equals(value.trim());
  }
}
