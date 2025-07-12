package com.hotel.webapp.dto.request;

import com.hotel.webapp.validation.FieldNotEmpty;
import jakarta.annotation.Nullable;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressDTO {
  @FieldNotEmpty(field = "Province code")
  String provinceCode;
  @FieldNotEmpty(field = "District code")
  String districtCode;
  @FieldNotEmpty(field = "Ward code")
  String wardCode;
  @FieldNotEmpty(field = "Street id")
  Integer streetId;
  @FieldNotEmpty(field = "Street number")
  String streetNumber;
  String note;
}


