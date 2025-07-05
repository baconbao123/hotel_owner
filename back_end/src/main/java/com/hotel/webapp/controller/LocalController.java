package com.hotel.webapp.controller;

import com.hotel.webapp.dto.response.ApiResponse;
import com.hotel.webapp.dto.response.LocalResponse;
import com.hotel.webapp.service.owner.LocalService;
import jakarta.validation.constraints.NotBlank;
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
  LocalService localService;

  @GetMapping("/get-district")
  public ApiResponse<List<LocalResponse>> getDistrict(
        @RequestParam @NotBlank(message = "PROVINCE_CODE_REQUIRED") String provinceCode) {
    return ApiResponse.<List<LocalResponse>>builder()
                      .result(localService.findDistrictsByProvinceCode(provinceCode))
                      .build();
  }

  @GetMapping("/get-ward")
  public ApiResponse<List<LocalResponse>> getWard(
        @RequestParam @NotBlank(message = "DISTRICT_CODE_REQUIRED") String districtCode) {
    return ApiResponse.<List<LocalResponse>>builder()
                      .result(localService.findWardsByDistrict(districtCode))
                      .build();
  }

  @GetMapping("/get-street")
  public ApiResponse<List<LocalResponse.StreetResponse>> getStreet(@RequestParam
  @NotBlank(message = "WARD_CODE_REQUIRED") String wardCode) {
    return ApiResponse.<List<LocalResponse.StreetResponse>>builder()
                      .result(localService.findStreetByWard(wardCode))
                      .build();
  }

  @GetMapping("/get-ward-info")
  public ApiResponse<LocalResponse.WardInfoResponse> getWardInfo(
        @RequestParam @NotBlank(message = "WARD_CODE_REQUIRED") String wardCode) {
    return ApiResponse.<LocalResponse.WardInfoResponse>builder()
                      .result(localService.findWardInfoByWardCode(wardCode))
                      .build();
  }
}
