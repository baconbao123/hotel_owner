package com.hotel.webapp.dto.admin.request;

import com.hotel.webapp.dto.admin.request.properties.MapURProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MapURDTO {
  List<MapURProperties> properties;
}

