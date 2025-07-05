package com.hotel.webapp.dto.request;

import com.hotel.webapp.validation.FieldNotEmpty;
import com.hotel.webapp.validation.MaxSizeListImg;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomDTO {
  String name;

  String keepAvatar;
  MultipartFile roomAvatar;

  Integer hotelId;
  Integer roomNumber;
  BigDecimal roomArea;
  Integer roomType;
  BigDecimal priceHour;
  BigDecimal priceNight;
  Integer limitPerson;
  String description;
  @FieldNotEmpty(field = "status")
  Boolean status;

  List<Integer> facilities;

  @MaxSizeListImg(value = 3)
  List<ImagesReq> images;
}
