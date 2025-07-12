package com.hotel.webapp.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRes {
  Integer id;
  String fullName;
  String email;
  String phoneNumber;
  String avatarUrl;
  Boolean status;

  List<RoleRes> roles;

  String createdName;
  String updatedName;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;

  Integer userTypeId;
  String userTypeName;

  @Getter
  @Setter
  @AllArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class RoleRes {
    Integer roleId;
    String roleName;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class UserProfileRes {
    Integer id;
    String fullName;
    String email;
    String phoneNumber;
    String avatarUrl;
    List<String> roles;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class OwnerRes {
    Integer id;
    String fullName;
    String email;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class CustomerRes {
    Integer id;
    String fullName;
    String email;
  }
}
