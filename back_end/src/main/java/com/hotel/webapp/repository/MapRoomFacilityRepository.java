package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.MapRoomFacility;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MapRoomFacilityRepository extends BaseRepository<MapRoomFacility, Integer> {
  List<MapRoomFacility> findByRoomIdAndDeletedAtIsNull(Integer roomId);
}
