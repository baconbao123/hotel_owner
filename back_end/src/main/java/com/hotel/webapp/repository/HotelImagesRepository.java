package com.hotel.webapp.repository;

import com.hotel.webapp.entity.HotelImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelImagesRepository extends JpaRepository<HotelImages, Integer> {
  List<HotelImages> findAllByHotelIdAndDeletedAtIsNull(Integer hotelId);
}
