package com.hotel.webapp.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomRes {
  Integer id;
  String name;
  String roomAvatar;
  String hotelName;
  BigDecimal roomArea;
  Integer roomNumber;
  String roomType;
  BigDecimal priceHours;
  BigDecimal priceNight;
  Integer limitPerson;
  String description;
  Boolean status;
  String createdName;
  String updatedName;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
