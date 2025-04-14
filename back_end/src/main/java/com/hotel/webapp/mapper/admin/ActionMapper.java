package com.hotel.webapp.mapper.admin;

import com.hotel.webapp.base.BaseMapper;
import com.hotel.webapp.dto.admin.request.ActionResourceDTO;
import com.hotel.webapp.entity.Actions;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ActionMapper extends BaseMapper<Actions, ActionResourceDTO> {

  @Override
  Actions toCreate(ActionResourceDTO actionResourceDTO);

  @Override
  void toUpdate(@MappingTarget Actions actions, ActionResourceDTO actionResourceDTO);
}
