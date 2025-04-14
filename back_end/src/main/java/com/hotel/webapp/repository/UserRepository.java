package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Integer> {
  //  Startup - for sa
  Optional<User> findByEmail(String email);

  boolean existsByEmailAndDeletedAtIsNull(String email);

  boolean existsByEmailAndIdNotAndDeletedAtIsNull(String email, int id);

  boolean existsByIdAndIsActiveIsTrueAndDeletedAtIsNull(Integer id);
}
