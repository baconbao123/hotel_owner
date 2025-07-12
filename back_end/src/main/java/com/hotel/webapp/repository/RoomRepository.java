package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.dto.response.RoomRes;
import com.hotel.webapp.entity.Rooms;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends BaseRepository<Rooms, Integer> {
  // rooms
  @Query("select r from Rooms r where r.hotelId = :hotelId and r.deletedAt is null")
  Page<Rooms> findByHotelId(Integer hotelId, Specification<Rooms> spec, Pageable pageable);

  @Query("select new com.hotel.webapp.dto.response.RoomRes(r.id, r.name, r.roomAvatar, h.name, r.roomArea, " +
        "r.roomNumber, rt.name, r.priceHour, r.priceNight, r.limitPerson, r.description, r.status, u1.fullName, " +
        "u2.fullName, r.createdAt, r.updatedAt)" +
        "from Rooms r " +
        "join Hotels h on h.id = r.hotelId " +
        "join RoomType rt on rt.id = r.roomType " +
        "left join User u1 on r.createdBy = u1.id " +
        "left join User u2 on r.updatedBy = u2.id " +
        "where r.id = :id and r.deletedAt is null ")
  RoomRes findRoomById(Integer id);
}
