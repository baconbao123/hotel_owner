package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.RoomImages;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomImagesRepository extends BaseRepository<RoomImages, Integer> {
  List<RoomImages> findAllByRoomIdAndDeletedAtIsNull(Integer roomId);
}
