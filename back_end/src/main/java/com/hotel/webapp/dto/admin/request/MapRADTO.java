package com.hotel.webapp.dto.admin.request;

import com.hotel.webapp.dto.admin.request.properties.MapRAPropertiesDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MapRADTO {
  List<MapRAPropertiesDTO> properties;
}
