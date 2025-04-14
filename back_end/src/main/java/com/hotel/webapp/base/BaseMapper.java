package com.hotel.webapp.base;

import org.mapstruct.MappingTarget;

public interface BaseMapper<E, DTO> {
  E toCreate(DTO create);

  void toUpdate(@MappingTarget E target, DTO update);
}
