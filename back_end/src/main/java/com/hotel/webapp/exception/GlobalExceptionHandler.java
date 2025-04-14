package com.hotel.webapp.exception;

import com.hotel.webapp.dto.admin.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = Exception.class)
  ResponseEntity<ApiResponse> runtimeExceptionHandler(Exception e) {
    ApiResponse apiResponse = new ApiResponse();
    apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
    apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
    return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode()).body(apiResponse);
  }

  @ExceptionHandler(value = AppException.class)
  ResponseEntity<ApiResponse> appExceptionHandler(AppException e) {
    ErrorCode errorCode = e.getErrorCode();
    ApiResponse apiResponse = new ApiResponse();
    apiResponse.setCode(errorCode.getCode());
    apiResponse.setMessage(errorCode.getMessage());
    return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException e) {
    FieldError fieldError = e.getFieldError();
    String defaultMessage = fieldError.getDefaultMessage(); // Ví dụ: "email_INVALID_REGEX"

    ErrorCode errorCode;
    String finalMessage;
    String fieldName;

    try {
      // Thử tìm ErrorCode tương ứng
      errorCode = ErrorCode.valueOf(defaultMessage);
      finalMessage = errorCode.getMessage();
    } catch (IllegalArgumentException ex) {
      if (defaultMessage != null && defaultMessage.endsWith("_NOT_EMPTY")) {
        errorCode = ErrorCode.FIELD_NOT_EMPTY;
        fieldName = extractFileName(defaultMessage, "_NOT_EMPTY");
        finalMessage = errorCode.getMessage().replace("{field}", fieldName);
      } else if (defaultMessage != null && defaultMessage.endsWith("_INVALID_REGEX")) {
        errorCode = ErrorCode.FIELD_INVALID;
        fieldName = extractFileName(defaultMessage, "_INVALID_REGEX");
        finalMessage = errorCode.getMessage().replace("{field}", fieldName);
      } else {
        errorCode = ErrorCode.KEY_INVALID;
        finalMessage = errorCode.getMessage();
      }
    }

    ApiResponse response = new ApiResponse();
    response.setCode(errorCode.getCode());
    response.setMessage(finalMessage);
    return ResponseEntity.status(errorCode.getStatusCode()).body(response);
  }


  @ExceptionHandler(value = AuthorizationDeniedException.class)
  ResponseEntity<ApiResponse> authorizationDeniedExceptionHandler(AuthorizationDeniedException e) {
    ApiResponse apiResponse = new ApiResponse();
    apiResponse.setCode(ErrorCode.ACCESS_DENIED.getCode());
    apiResponse.setMessage(ErrorCode.ACCESS_DENIED.getMessage());
    return ResponseEntity.status(ErrorCode.ACCESS_DENIED.getStatusCode()).body(apiResponse);
  }

  private String extractFileName(String defaultMess, String suffix) {
    String rawFileName = defaultMess.replace(suffix, "");
    return rawFileName.substring(0, 1).toUpperCase() + rawFileName.substring(1).toLowerCase();
  }
}
