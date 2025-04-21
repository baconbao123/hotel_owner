package com.hotel.webapp.dto.admin.request;

import com.hotel.webapp.validation.FieldNotEmpty;
import com.hotel.webapp.validation.Trim;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
  @Trim
  String fullName;
  @Email(message = "EMAIL_INVALID", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
  @Trim
  String email;
  @Pattern(regexp = "^\\d{10}$")
  @Trim
  String phoneNumber;
  @FieldNotEmpty(field = "hotel id")
  Integer hotelId;
  String password;
  @Nullable
  MultipartFile avatarUrl;
  @Nullable
  Integer addressId;
  Boolean isActive;
}
