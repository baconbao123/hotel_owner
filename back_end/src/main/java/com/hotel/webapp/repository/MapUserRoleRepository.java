package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.MapUserRoles;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MapUserRoleRepository extends BaseRepository<MapUserRoles, Integer> {
  Optional<MapUserRoles> findByRoleIdAndUserId(int roleId, int userId);

  @Query("select mur.id from MapUserRoles mur where mur.roleId = :roleId and mur.userId = :userId")
  int findIdByRoleIdAndUserId(int roleId, int userId);

  List<MapUserRoles> findAllByUserId(int userId);

  List<MapUserRoles> findAllByRoleId(int roleId);

  void deleteByUserIdIn(Collection<Integer> userId);

  boolean existsByIdAndDeletedAtIsNull(Integer id);

  List<MapUserRoles> findAllByUserIdInAndDeletedAtIsNull(Collection<Integer> userId);


}
