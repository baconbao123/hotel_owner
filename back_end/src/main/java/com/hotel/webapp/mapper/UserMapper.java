package com.hotel.webapp.mapper;

import com.hotel.webapp.base.BaseMapper;
import com.hotel.webapp.dto.request.UserDTO;
import com.hotel.webapp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, UserDTO> {
  @Override
  @Mapping(target = "avatarUrl", ignore = true)
  @Mapping(target = "password", ignore = true)
  User toCreate(UserDTO userDTO);

  @Override
  @Mapping(target = "avatarUrl", ignore = true)
  @Mapping(target = "password", ignore = true)
  void toUpdate(@MappingTarget User user, UserDTO userUpdate);
}
