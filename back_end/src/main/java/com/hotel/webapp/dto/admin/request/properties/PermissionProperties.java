package com.hotel.webapp.dto.admin.request.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionProperties {
  Integer mapUserRolesId;
  List<Integer> mapResourcesActionId;
}
