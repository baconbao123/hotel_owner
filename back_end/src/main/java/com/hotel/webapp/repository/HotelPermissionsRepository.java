package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.HotelPermissions;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Repository
public interface HotelPermissionsRepository extends BaseRepository<HotelPermissions, Integer> {
  int countPermissionsByMapResourcesActionIdAndMapUserRolesId(int mapResourcesActionId, int mapUserRolesId);

  @Modifying
  @Transactional
  @Query("insert into HotelPermissions (mapResourcesActionId, mapUserRolesId, createdAt, createdBy) values " +
        "(:mapResourcesActionId, :mapUserRolesId, :createdAt, :createdBy)")
  void insertPermissions(int mapResourcesActionId, int mapUserRolesId, Timestamp createdAt, int createdBy);

  void deleteByMapUserRolesIdIn(Collection<Integer> ids);

  List<HotelPermissions> findByMapUserRolesId(int mapUserRolesId);
  List<HotelPermissions> findByMapResourcesActionId(int mapResourcesActionId);

  @Query("select p from HotelPermissions p where p.mapUserRolesId in :mapURs and p.deletedAt is null")
  List<HotelPermissions> findAllByMapURId(@Param("mapURs") Collection<Integer> mapURs);

  @Query("select p from HotelPermissions p where p.mapResourcesActionId in :mapRAs and p.deletedAt is null")
  List<HotelPermissions> findAllByMapRAId(@Param("mapRAs") Collection<Integer> c);

}
