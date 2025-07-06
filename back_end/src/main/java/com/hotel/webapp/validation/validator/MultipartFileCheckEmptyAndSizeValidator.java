package com.hotel.webapp.validation.validator;

import com.hotel.webapp.dto.request.HotelDTO.ImagesReq;
import com.hotel.webapp.validation.MultipartFileCheckEmptyAndSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class MultipartFileCheckEmptyAndSizeValidator implements ConstraintValidator<MultipartFileCheckEmptyAndSize, Object> {
  private boolean force;
  private String field;
  private int maxSize;

  @Override
  public void initialize(MultipartFileCheckEmptyAndSize constraintAnnotation) {
    this.force = constraintAnnotation.force();
    this.field = constraintAnnotation.field();
    this.maxSize = constraintAnnotation.value();
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    // Case 1: Handle null value
    if (value == null) {
      if (force) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(field + "_NOT_EMPTY")
               .addConstraintViolation();
        return false;
      }
      return true;
    }

    // Case 2: Single MultipartFile (e.g., avatar.avatarUrl, document.documentUrl)
    if (value instanceof MultipartFile file) {
      if (force && file.isEmpty()) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(field + "_NOT_EMPTY")
               .addConstraintViolation();
        return false;
      }
      return true;
    }

    // Case 3: List<ImagesReq> (e.g., images)
    if (value instanceof List<?> list) {
      long validFiles = list.stream()
                            .filter(item -> item instanceof ImagesReq)
                            .map(item -> (ImagesReq) item)
                            .filter(img -> img != null && img.getImageFile() != null && !img.getImageFile().isEmpty())
                            .count();

      // If force = true, require at least 1 valid file
      if (force && validFiles < 1) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(field + "_NOT_EMPTY")
               .addConstraintViolation();
        return false;
      }

      // Check max size limit
      if (validFiles > maxSize) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(field + "_SIZE_EXCEEDED")
               .addConstraintViolation();
        return false;
      }

      return true;
    }

    // Invalid type
    return false;
  }
}