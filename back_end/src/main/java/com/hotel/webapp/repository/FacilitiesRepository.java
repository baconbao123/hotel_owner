package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.Facilities;
import com.hotel.webapp.entity.FacilityType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacilitiesRepository extends BaseRepository<Facilities, Integer> {
  List<Facilities> findByIdAndDeletedAtIsNull(Integer id);

  @Query("select f.id, f.name, f.icon, f.type, ft.name, f.createdAt, f.updatedAt, u1.fullName, u2.fullName " +
        "from Facilities f " +
        "left join FacilityType ft on ft.id = f.type " +
        "left join User u1 on f.createdBy = u1.id " +
        "left join User u2 on f.updatedBy = u2.id " +
        "where f.id = :id and f.deletedAt is null")
  List<Object[]> findFacilityResById(Integer id);

  @Query("select f from Facilities f where f.deletedAt is null")
  List<Facilities> findAllFacilities();

  //  FacilityType
  @Query("select f from FacilityType f where f.deletedAt is null")
  List<FacilityType> findAllFacilityTYpe();

  @Query("select f from FacilityType f where f.id = :id and f.deletedAt is null")
  Optional<FacilityType> findFacilityTypeById(Integer id);

  //  FacilityType - seeder
  @Transactional
  @Modifying
  @Query("insert into FacilityType (name, colName, createdAt, createdBy) " +
        "values (:name, :colName, :createdAt, :createdBy)")
  void insertFacilityType(String name, String colName, LocalDateTime createdAt, Integer createdBy);

  @Query("select f from FacilityType f where f.name = :name and f.deletedAt is null")
  Optional<FacilityType> findFacilityTypeByName(String name);

  // for user - home
  // find Facilities By HotelIds
  @Query("select mhf.hotelId, f.id, f.name, f.icon " +
        "from Facilities f  " +
        "left join MapHotelFacility mhf on mhf.facilityId = f.id " +
        "where mhf.hotelId in :hotelId and mhf.deletedAt is null")
  List<Object[]> findFacilitiesByHotelIds(List<Integer> hotelId);

  // find Facilities By HotelId
  @Query("select mhf.hotelId, f.id, f.name, f.icon " +
        "from Facilities f  " +
        "left join MapHotelFacility mhf on mhf.facilityId = f.id " +
        "where mhf.hotelId = :hotelId and mhf.deletedAt is null")
  List<Object[]> findFacilitiesByHotelId(Integer hotelId);

  // find Facilities By RoomId
  @Query("select mrf.roomId, f.id, f.name, f.icon " +
        "from Facilities f  " +
        "left join MapRoomFacility mrf on mrf.facilityId = f.id " +
        "where mrf.roomId = :roomId and mrf.deletedAt is null")
  List<Object[]> findFacilitiesByRoomId(Integer roomId);


  // filters
  @Query("select f.id, f.name, f.icon " +
        "from Facilities f " +
        "where f.deletedAt is null")
  List<Object[]> findFacilities();
}

