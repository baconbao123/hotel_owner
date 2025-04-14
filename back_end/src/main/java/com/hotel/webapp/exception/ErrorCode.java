package com.hotel.webapp.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ErrorCode {
  UNCATEGORIZED_EXCEPTION(9999, "Unauthorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
  KEY_INVALID(9998, "Invalid key", HttpStatus.BAD_REQUEST),
  //  Exists - Bad Request
  ACTION_EXISTED(400, "Action already exists", HttpStatus.BAD_REQUEST),
  RESOURCE_EXISTED(400, "Resource already exists", HttpStatus.BAD_REQUEST),
  ROLE_EXISTED(400, "Role already exists", HttpStatus.BAD_REQUEST),
  EMAIL_EXISTED(400, "Email already exists", HttpStatus.BAD_REQUEST),
  // Field Invalid - 400
  EMAIL_INVALID(400, "Email is not valid", HttpStatus.BAD_REQUEST),
  FIELD_INVALID(400, "{field} invalid", HttpStatus.BAD_REQUEST),
  // Not Active - 400
  ROLE_NOT_ACTIVE(400, "Role is not active", HttpStatus.BAD_REQUEST),
  USER_NOT_ACTIVE(400, "User is not active", HttpStatus.BAD_REQUEST),
  ACTION_NOT_ACTIVE(400, "Action is not active", HttpStatus.BAD_REQUEST),
  RESOURCE_NOT_ACTIVE(400, "Resource is not active", HttpStatus.BAD_REQUEST),
  MAPPING_UR_NOT_ACTIVE(400, "Mapping User Role is not active", HttpStatus.NOT_FOUND),
  MAPPING_RA_NOT_ACTIVE(400, "Mapping Resource Action is not active", HttpStatus.NOT_FOUND),

  //  NOT EMPTY - 400
  FIELD_NOT_EMPTY(400, "{field} cannot be empty", HttpStatus.BAD_REQUEST),

  CREATION_FAILED(400, "Creation failed", HttpStatus.BAD_REQUEST),
  UPDATED_FAILED(400, "Updated failed", HttpStatus.BAD_REQUEST),


  //  Invalid - 401
  UNAUTHENTICATED(401, "Unauthenticated", HttpStatus.UNAUTHORIZED),
  AUTHENTICATION_FAILED(401, "Invalid email or password", HttpStatus.UNAUTHORIZED),
  ACCESS_DENIED(403, "Access Denied", HttpStatus.FORBIDDEN),

  //  Not Found - 404
  ACTION_NOTFOUND(404, "Action Not Found", HttpStatus.NOT_FOUND),
  USER_NOTFOUND(404, "User Not Found", HttpStatus.NOT_FOUND),
  ROLE_NOTFOUND(404, "Role Not Found", HttpStatus.NOT_FOUND),
  RESOURCE_NOTFOUND(404, "Resource Not Found", HttpStatus.NOT_FOUND),
  PERMISSION_NOTFOUND(404, "Permission Not Found", HttpStatus.NOT_FOUND),


  // Mapping Not found - 404
  MAPPING_UR_NOTFOUND(404, "Mapping User Role Not Found", HttpStatus.NOT_FOUND),
  MAPPING_RA_NOTFOUND(404, "Mapping Resource Action Not Found", HttpStatus.NOT_FOUND)
  ;

  int code;
  String message;
  HttpStatusCode statusCode;

}
