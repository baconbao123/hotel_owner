package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.Permissions;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Repository
public interface PermissionsRepository extends BaseRepository<Permissions, Integer> {
  int countPermissionsByMapResourcesActionIdAndMapUserRolesId(int mapResourcesActionId, int mapUserRolesId);

  @Modifying
  @Transactional
  @Query("insert into Permissions (mapResourcesActionId, mapUserRolesId, createdAt, createdBy) values " +
        "(:mapResourcesActionId, :mapUserRolesId, :createdAt, :createdBy)")
  void insertPermissions(int mapResourcesActionId, int mapUserRolesId, Timestamp createdAt, int createdBy);

  void deleteByMapUserRolesIdIn(Collection<Integer> ids);

  List<Permissions> findByMapUserRolesId(int mapUserRolesId);
  List<Permissions> findByMapResourcesActionId(int mapResourcesActionId);

  @Query("select p from Permissions p where p.mapUserRolesId in :mapURs and p.deletedAt is null")
  List<Permissions> findAllByMapURId(@Param("mapURs") Collection<Integer> mapURs);

  @Query("select p from Permissions p where p.mapResourcesActionId in :mapRAs and p.deletedAt is null")
  List<Permissions> findAllByMapRAId(@Param("mapRAs") Collection<Integer> c);

}
