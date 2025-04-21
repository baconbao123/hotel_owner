package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.HotelRoles;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelRoleRepository extends BaseRepository<HotelRoles, Integer> {
  Optional<HotelRoles> findByName(String name);

  boolean existsByNameAndDeletedAtIsNull(String name);

  boolean existsByNameAndIdNotAndDeletedAtIsNull(String name, int id);

  boolean existsByIdAndIsActiveIsTrueAndDeletedAtIsNull(int id);
}
