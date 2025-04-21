package com.hotel.webapp.mapper.admin;

import com.hotel.webapp.base.BaseMapper;
import com.hotel.webapp.dto.admin.request.NameDTO;
import com.hotel.webapp.entity.HotelActions;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HotelActionMapper extends BaseMapper<HotelActions, NameDTO> {

  @Override
  HotelActions toCreate(NameDTO nameDTO);

  @Override
  void toUpdate(@MappingTarget HotelActions hotelActions, NameDTO nameDTO);
}
