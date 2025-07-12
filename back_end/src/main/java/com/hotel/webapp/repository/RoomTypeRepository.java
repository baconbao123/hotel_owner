package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.RoomType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomTypeRepository extends BaseRepository<RoomType, Integer> {
  //  find-all
  @Query("select t from RoomType t where t.deletedAt is null")
  List<RoomType> findRoomTypes();
}
