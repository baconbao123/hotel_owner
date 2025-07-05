package com.hotel.webapp.mapper;

import com.hotel.webapp.base.BaseMapper;
import com.hotel.webapp.dto.request.RoomDTO;
import com.hotel.webapp.entity.Rooms;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper extends BaseMapper<Rooms, RoomDTO> {
  @Override
  @Mapping(target = "roomAvatar", ignore = true)
  Rooms toCreate(RoomDTO create);

  @Override
  @Mapping(target = "roomAvatar", ignore = true)
  void toUpdate(@MappingTarget Rooms target, RoomDTO update);
}
