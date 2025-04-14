package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.Actions;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface ActionRepository extends BaseRepository<Actions, Integer> {
  @Query("SELECT a.id FROM Actions a WHERE a.name = :name")
  Optional<Integer> findIdByName(String name);

  @Modifying
  @Transactional
  @Query("insert into Actions (name, createdAt, createdBy) values (:name, :createdAt, :createdBy)")
  void insetActions(String name, Timestamp createdAt, int createdBy);

  boolean existsByNameAndDeletedAtIsNull(String name);

  boolean existsByNameAndIdNotAndDeletedAtIsNull(String name, Integer id);

  boolean existsByIdAndDeletedAtIsNull(Integer id);
}
