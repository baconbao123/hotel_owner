package com.hotel.webapp.repository;

import com.hotel.webapp.dto.response.LocalResponse;
import com.hotel.webapp.entity.Districts;
import com.hotel.webapp.entity.Provinces;
import com.hotel.webapp.entity.Streets;
import com.hotel.webapp.entity.Wards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalRepository extends JpaRepository<Provinces, Integer> {
  @Query("select s.id, s.name " +
        "from Streets s " +
        "where s.wardCode = :wardCode and s.deletedAt is null " +
        "order by s.name asc")
  List<Object[]> findStreetByWard(String wardCode);

  @Query("select s from Streets s where s.id = :id and s.deletedAt is null")
  Optional<Streets> findStreetsById(Integer id);

  // province
  @Query("select new com.hotel.webapp.dto.response.LocalResponse(p.code, p.name) " +
        "from Provinces p " +
        "order by p.name asc")
  List<LocalResponse> findProvinces();

  @Query("select case when count(p) > 0 then true else false end " +
        "from Provinces p " +
        "where p.code = :code")
  boolean existsProvincesByCode(String code);

  @Query("select p from Provinces p where p.code = :code")
  Optional<Provinces> findProvinceByCode(String code);

  // district
  @Query("select new com.hotel.webapp.dto.response.LocalResponse(d.code, d.name) " +
        "from Districts d " +
        "where d.provinceCode = :provinceCode " +
        "order by d.name asc ")
  List<LocalResponse> findDistrictsByProvinceCode(String provinceCode);

  @Query("select d from Districts d where d.code = :code")
  Optional<Districts> findDistrictByCode(String code);

  // ward
  @Query("select new com.hotel.webapp.dto.response.LocalResponse(w.code, w.name) " +
        "from Wards w " +
        "where w.districtCode = :districtCode " +
        "order by w.name asc")
  List<LocalResponse> findWardsByDistrict(String districtCode);

  @Query("select case when count(w) > 0 then true else false end " +
        "from Wards w " +
        "where w.code = :code")
  boolean existsWardByCode(String code);

  @Query("select w from Wards w where w.code = :code")
  Optional<Wards> findWardByWardCode(String code);
}
