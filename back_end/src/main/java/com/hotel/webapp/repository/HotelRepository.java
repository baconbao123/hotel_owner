package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.Hotels;
import com.hotel.webapp.entity.TypeHotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends BaseRepository<Hotels, Integer> {
  @Query("select h.id, h.name, h.description, h.status, u1.fullName, u2.fullName, h.createdAt, h.updatedAt, " +
        "h.avatar, a.streetNumber, a.streetId, a.wardCode, w.name, a.districtCode, d.name, " +
        "a.provinceCode, p.name, s.name, a.note, h.note, o.fullName, o.id " +
        "from Hotels h " +
        "left join Address a on a.id = h.addressId " +
        "left join User o on h.ownerId = o.id " +
        "join Streets s on a.streetId = s.id " +
        "join Wards w on a.wardCode = w.code " +
        "join Districts d on a.districtCode = d.code " +
        "join Provinces p on a.provinceCode = p.code " +
        "left join User u1 on h.createdBy = u1.id " +
        "left join User u2 on h.updatedBy = u2.id " +
        "where h.id = :id and h.deletedAt is null"
  )
  List<Object[]> findHotel(Integer id);

  @Query("select hi.id, hi.name " +
        "from HotelImages hi " +
        "join Hotels h on h.id = hi.hotelId " +
        "where hi.hotelId = :hotelId and hi.deletedAt is null")
  List<Object[]> getHotelImagesByHotelId(Integer hotelId);

  @Query("select th.id, th.name " +
        "from TypeHotel th " +
        "left join MapHotelType mht on mht.typeId = th.id and mht.hotelId = :hotelId " +
        "where mht.hotelId = :hotelId and mht.deletedAt is null and th.deletedAt is null")
  List<Object[]> getTypeHotelsByHotelId(Integer hotelId);

  @Query("select fa.id, fa.name, fa.icon " +
        "from Facilities fa " +
        "left join MapHotelFacility mhf on mhf.facilityId = fa.id and mhf.hotelId = :hotelId " +
        "where mhf.hotelId = :hotelId and mhf.deletedAt is null and fa.deletedAt is null")
  List<Object[]> getFacilitiesByHotelId(Integer hotelId);

  @Query("select dh.id, dh.name, dh.typeId, dt.name, dh.documentUrl " +
        "from DocumentsHotel dh " +
        "join DocumentType dt on dh.typeId = dt.id " +
        "where dh.hotelId = :hotelId and dh.deletedAt is null")
  List<Object[]> getDocumentsByHotelId(Integer hotelId);

  @Query("select hp.id, hp.name, hp.description " +
        "from HotelPolicy hp " +
        "where hp.hotelId = :hotelId and hp.deletedAt is null")
  List<Object[]> getPolicyByHotelId(Integer hotelId);

  // TypeHotel
  @Query("select t from TypeHotel t where t.deletedAt is null")
  List<TypeHotel> findAllTypeHotel();

  @Query("select h from Hotels h " +
        "where h.ownerId = :ownerId and h.deletedAt is null")
  Page<Hotels> findAllByOwnerId(Integer ownerId, Specification<Hotels> specification, Pageable pageRequest);
}
