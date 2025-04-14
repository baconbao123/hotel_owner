package com.hotel.webapp.service.admin;

import com.hotel.webapp.dto.admin.response.LocalResponse;
import com.hotel.webapp.repository.DistrictRepository;
import com.hotel.webapp.repository.ProvincesRepository;
import com.hotel.webapp.repository.WardRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalServiceImpl {
  ProvincesRepository provincesRepository;
  WardRepository wardRepository;
  DistrictRepository districtRepository;

  public List<LocalResponse> getProvinces() {
    return provincesRepository.findProvinces();
  }

  public List<LocalResponse> findDistrictsByProvinceCode(String provinceCode) {
    return districtRepository.findDistrictsByProvinceCode(provinceCode);
  }

  public List<LocalResponse> findWardsByDistrict(String districtCode) {
    return wardRepository.findWardsByDistrict(districtCode);
  }
}
