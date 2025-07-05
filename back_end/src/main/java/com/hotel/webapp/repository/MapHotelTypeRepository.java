package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.MapHotelType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MapHotelTypeRepository extends BaseRepository<MapHotelType, Integer> {
  List<MapHotelType> findAllByHotelIdAndDeletedAtIsNull(Integer hotelId);
}
