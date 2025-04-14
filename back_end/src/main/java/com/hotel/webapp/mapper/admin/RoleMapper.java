package com.hotel.webapp.mapper.admin;

import com.hotel.webapp.base.BaseMapper;
import com.hotel.webapp.dto.admin.request.RoleDTO;
import com.hotel.webapp.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper extends BaseMapper<Role, RoleDTO> {
  @Override
  Role toCreate(RoleDTO roleDTO);

  @Override
  void toUpdate(@MappingTarget Role role, RoleDTO roleUpdate);
}
