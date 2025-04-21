package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.HotelMapUserRoles;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface HotelMapUserRoleRepository extends BaseRepository<HotelMapUserRoles, Integer> {
  Optional<HotelMapUserRoles> findByRoleIdAndUserId(int roleId, int userId);

  @Query("select mur.id from HotelMapUserRoles mur where mur.roleId = :roleId and mur.userId = :userId")
  int findIdByRoleIdAndUserId(int roleId, int userId);

  List<HotelMapUserRoles> findAllByUserId(int userId);

  List<HotelMapUserRoles> findAllByRoleId(int roleId);

  void deleteByUserIdIn(Collection<Integer> userId);

  boolean existsByIdAndDeletedAtIsNull(Integer id);

  List<HotelMapUserRoles> findAllByUserIdInAndDeletedAtIsNull(Collection<Integer> userId);


}
