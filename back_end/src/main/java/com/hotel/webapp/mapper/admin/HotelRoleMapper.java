package com.hotel.webapp.mapper.admin;

import com.hotel.webapp.base.BaseMapper;
import com.hotel.webapp.dto.admin.request.RoleDTO;
import com.hotel.webapp.entity.HotelRoles;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HotelRoleMapper extends BaseMapper<HotelRoles, RoleDTO> {
  @Override
  HotelRoles toCreate(RoleDTO roleDTO);

  @Override
  void toUpdate(@MappingTarget HotelRoles hotelRoles, RoleDTO roleUpdate);
}
