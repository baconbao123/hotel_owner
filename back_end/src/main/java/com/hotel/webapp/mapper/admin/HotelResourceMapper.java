package com.hotel.webapp.mapper.admin;

import com.hotel.webapp.base.BaseMapper;
import com.hotel.webapp.dto.admin.request.NameDTO;
import com.hotel.webapp.entity.HotelResources;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HotelResourceMapper extends BaseMapper<HotelResources, NameDTO> {

  @Override
  HotelResources toCreate(NameDTO create);

  @Override
  void toUpdate(@MappingTarget HotelResources target, NameDTO update);
}
