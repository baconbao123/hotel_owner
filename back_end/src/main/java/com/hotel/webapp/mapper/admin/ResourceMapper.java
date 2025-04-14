package com.hotel.webapp.mapper.admin;

import com.hotel.webapp.base.BaseMapper;
import com.hotel.webapp.dto.admin.request.ActionResourceDTO;
import com.hotel.webapp.entity.Resources;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ResourceMapper extends BaseMapper<Resources, ActionResourceDTO> {

  @Override
  Resources toCreate(ActionResourceDTO create);

  @Override
  void toUpdate(@MappingTarget Resources target, ActionResourceDTO update);
}
