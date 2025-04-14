package com.hotel.webapp.dto.admin.request;

import com.hotel.webapp.validation.Trim;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleDTO {
  @Trim
  String name;
  @Nullable
  String description;
  Boolean isActive;

}
