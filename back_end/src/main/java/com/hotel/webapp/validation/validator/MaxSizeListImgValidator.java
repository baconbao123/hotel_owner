package com.hotel.webapp.validation.validator;

import com.hotel.webapp.validation.MaxSizeListImg;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class MaxSizeListImgValidator implements ConstraintValidator<MaxSizeListImg, List<?>> {
  private int maxSize;

  @Override
  public void initialize(MaxSizeListImg constraintAnnotation) {
    this.maxSize = constraintAnnotation.value();
  }

  @Override
  public boolean isValid(List<?> value, ConstraintValidatorContext context) {
    if (value == null) return true;
    return value.size() <= maxSize;
  }
}

