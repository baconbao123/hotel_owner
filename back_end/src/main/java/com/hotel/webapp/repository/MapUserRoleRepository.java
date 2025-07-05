package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.MapUserRoles;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MapUserRoleRepository extends BaseRepository<MapUserRoles, Integer> {
  // start app
  Optional<MapUserRoles> findByRoleIdAndUserId(int roleId, int userId);

  //  find all
  List<MapUserRoles> findAllByUserIdAndDeletedAtIsNull(int userId);
}
