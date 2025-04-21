package com.hotel.webapp.mapper.admin;

import com.hotel.webapp.base.BaseMapper;
import com.hotel.webapp.dto.admin.request.UserDTO;
import com.hotel.webapp.entity.HotelUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HotelUserMapper extends BaseMapper<HotelUser, UserDTO> {
  @Override
  @Mapping(target = "avatarUrl", ignore = true)
  @Mapping(target = "password", ignore = true)
  HotelUser toCreate(UserDTO userDTO);

  @Override
  @Mapping(target = "avatarUrl", ignore = true)
  @Mapping(target = "password", ignore = true)
  void toUpdate(@MappingTarget HotelUser hotelUser, UserDTO userUpdate);
}
