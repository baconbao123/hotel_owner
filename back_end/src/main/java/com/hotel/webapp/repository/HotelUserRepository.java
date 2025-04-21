package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.HotelUser;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelUserRepository extends BaseRepository<HotelUser, Integer> {
  //  Startup - for sa
  Optional<HotelUser> findByEmail(String email);

  boolean existsByEmailAndDeletedAtIsNull(String email);

  boolean existsByEmailAndIdNotAndDeletedAtIsNull(String email, int id);

  boolean existsByIdAndIsActiveIsTrueAndDeletedAtIsNull(Integer id);
}
