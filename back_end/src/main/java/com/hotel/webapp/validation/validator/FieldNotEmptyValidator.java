package com.hotel.webapp.validation.validator;

import com.hotel.webapp.validation.FieldNotEmpty;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FieldNotEmptyValidator implements ConstraintValidator<FieldNotEmpty, Object> {
  private String fieldName;

  @Override
  public void initialize(FieldNotEmpty constraintAnnotation) {
    this.fieldName = constraintAnnotation.field();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    if (value == null || value.toString().trim().isEmpty()) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(fieldName + "_NOT_EMPTY")
             .addConstraintViolation();
      return false;
    }
    return true;
  }
}
