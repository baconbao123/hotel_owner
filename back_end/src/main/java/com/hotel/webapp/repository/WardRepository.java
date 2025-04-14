package com.hotel.webapp.repository;

import com.hotel.webapp.dto.admin.response.LocalResponse;
import com.hotel.webapp.entity.Wards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WardRepository extends JpaRepository<Wards, String> {
  @Query("select new com.hotel.webapp.dto.admin.response.LocalResponse(w.code, w.name) from Wards w " +
        "where w.districtCode = :districtCode")
  List<LocalResponse> findWardsByDistrict(String districtCode);
}
