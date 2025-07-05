package com.hotel.webapp.mapper;

import com.hotel.webapp.base.BaseMapper;
import com.hotel.webapp.dto.request.HotelDTO;
import com.hotel.webapp.entity.Hotels;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HotelMapper extends BaseMapper<Hotels, HotelDTO> {
  @Override
  @Mapping(target = "ownerId", ignore = true)
  @Mapping(target = "avatar", ignore = true)
  @Mapping(target = "addressId", ignore = true)
  @Mapping(target = "policyId", ignore = true)
  @Mapping(target = "approveId", ignore = true)
  @Mapping(target = "note", ignore = true)
  Hotels toCreate(HotelDTO create);

  @Override
  @Mapping(target = "ownerId", ignore = true)
  @Mapping(target = "avatar", ignore = true)
  @Mapping(target = "addressId", ignore = true)
  @Mapping(target = "policyId", ignore = true)
  @Mapping(target = "approveId", ignore = true)
  @Mapping(target = "note", ignore = true)
  void toUpdate(@MappingTarget Hotels target, HotelDTO update);
}
