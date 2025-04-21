package com.hotel.webapp.repository.shared;

import com.hotel.webapp.base.BaseRepository;
import com.hotel.webapp.entity.shared.Hotels;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelsRepository extends BaseRepository<Hotels, Integer> {
  boolean existsByIdAndDeletedAtIsNull(Integer id);
}
