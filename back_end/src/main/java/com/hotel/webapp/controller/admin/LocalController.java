package com.hotel.webapp.controller.admin;

import com.hotel.webapp.dto.admin.response.ApiResponse;
import com.hotel.webapp.dto.admin.response.LocalResponse;
import com.hotel.webapp.entity.Districts;
import com.hotel.webapp.entity.Provinces;
import com.hotel.webapp.entity.Wards;
import com.hotel.webapp.service.admin.LocalServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/local")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalController {
  LocalServiceImpl localService;

  @GetMapping("/provinces")
  public ApiResponse<List<LocalResponse>> getProvinces() {
    return ApiResponse.<List<LocalResponse>>builder()
                      .result(localService.getProvinces())
                      .build();
  }

  @GetMapping("/districts")
  public ApiResponse<List<LocalResponse>> getDistricts(@RequestParam String provinceCode) {
    return ApiResponse.<List<LocalResponse>>builder()
                      .result(localService.findDistrictsByProvinceCode(provinceCode))
                      .build();
  }

  @GetMapping("/wards")
  public ApiResponse<List<LocalResponse>> getWards(@RequestParam String districtCode) {
    return ApiResponse.<List<LocalResponse>>builder()
                      .result(localService.findWardsByDistrict(districtCode))
                      .build();
  }
}
