package com.hotel.webapp.dto.request;

import com.hotel.webapp.validation.FieldNotEmpty;
import com.hotel.webapp.validation.Trim;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthReq {
  @Email(message = "EMAIL_INVALID", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
  @Trim
  String email;
  @FieldNotEmpty(field = "Password")
  String password;
  @NotNull
  Boolean remember = false;

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class IntrospectRequest {
    String token;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class TokenRefreshReq {
    String refreshToken;
  }
}
