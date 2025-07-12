package com.hotel.webapp.service.owner;

import com.hotel.webapp.dto.response.LocalResponse;
import com.hotel.webapp.entity.Districts;
import com.hotel.webapp.entity.Provinces;
import com.hotel.webapp.entity.Wards;
import com.hotel.webapp.exception.AppException;
import com.hotel.webapp.exception.ErrorCode;
import com.hotel.webapp.repository.LocalRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalService {
  LocalRepository repository;

  public List<LocalResponse> getProvinces() {
    return repository.findProvinces();
  }

  public List<LocalResponse> findDistrictsByProvinceCode(String provinceCode) {
    return repository.findDistrictsByProvinceCode(provinceCode);
  }

  public List<LocalResponse> findWardsByDistrict(String districtCode) {
    return repository.findWardsByDistrict(districtCode);
  }

  public List<LocalResponse.StreetResponse> findStreetByWard(String wardCode) {
    List<Object[]> results = repository.findStreetByWard(wardCode);

    return results.stream()
                  .map(result -> new LocalResponse.StreetResponse((Integer) result[0], (String) result[1]))
                  .toList();
  }

  public LocalResponse.WardInfoResponse findWardInfoByWardCode(String wardCode) {
    Wards wards = repository.findWardByWardCode(wardCode)
                            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Ward"));

    Districts district = repository.findDistrictByCode(wards.getDistrictCode())
                                   .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "District"));

    Provinces province = repository.findProvinceByCode(district.getProvinceCode())
                                   .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Province"));

    return new LocalResponse.WardInfoResponse(
          province.getCode(),
          district.getCode(),
          wards.getCode(),
          wards.getName()
    );
  }
}
