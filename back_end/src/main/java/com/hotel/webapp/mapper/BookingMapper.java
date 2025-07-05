package com.hotel.webapp.mapper;

import com.hotel.webapp.base.BaseMapper;
import com.hotel.webapp.dto.request.BookingDTO;
import com.hotel.webapp.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookingMapper extends BaseMapper<Booking, BookingDTO> {
  @Override
  @Mapping(target = "actualCheckInTime", ignore = true)
  @Mapping(target = "actualCheckOutTime", ignore = true)
  Booking toCreate(BookingDTO dto);

  @Override
  @Mapping(target = "actualCheckInTime", ignore = true)
  @Mapping(target = "actualCheckOutTime", ignore = true)
  void toUpdate(@MappingTarget Booking e, BookingDTO bookingDTO);
}
