package com.hotel.webapp.repository;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.HotelResources;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface HotelResourcesRepository extends BaseRepository<HotelResources, Integer> {
  @Query("SELECT r.id FROM HotelResources r WHERE r.name = :name")
  Optional<Integer> findIdByName(String name);

  @Modifying
  @Transactional
  @Query("insert into HotelResources (name, createdAt, createdBy) values (:name, :createdAt, :createdBy)")
  void insertResources(String name, Timestamp createdAt, int createdBy);

  boolean existsByNameAndDeletedAtIsNull(String name);

  boolean existsByNameAndIdNotAndDeletedAtIsNull(String name, int id);

  boolean existsByIdAndDeletedAtIsNull(Integer id);
}
