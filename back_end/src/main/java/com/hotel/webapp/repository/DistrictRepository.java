package com.hotel.webapp.repository;

import com.hotel.webapp.dto.admin.response.LocalResponse;
import com.hotel.webapp.entity.Districts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DistrictRepository extends JpaRepository<Districts, String> {

  @Query("select new com.hotel.webapp.dto.admin.response.LocalResponse(d.code, d.name) from Districts d " +
        "where d.provinceCode = :provinceCode")
  List<LocalResponse> findDistrictsByProvinceCode(String provinceCode);
}
