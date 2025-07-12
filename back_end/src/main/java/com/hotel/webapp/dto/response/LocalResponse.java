package com.hotel.webapp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocalResponse {
  String code;
  String name;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class StreetResponse {
    Integer id;
    String name;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class WardInfoResponse {
    String provinceCode;
    String districtCode;
    String wardCode;
    String name;
  }
}
