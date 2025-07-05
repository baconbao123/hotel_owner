package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends BaseRepository<Role, Integer> {
  Optional<Role> findByName(String name);

  boolean existsByNameAndDeletedAtIsNull(String name);

  boolean existsByNameAndIdNotAndDeletedAtIsNull(String name, int id);

  @Query("select r from Role r where r.deletedAt is null")
  List<Role> findAllByDeletedAtIsNull();

  @Query("select r from Role r where r.id = :roleId and r.deletedAt is null")
  Optional<Role> findRolesByDeletedAtIsNull(Integer roleId);

}
