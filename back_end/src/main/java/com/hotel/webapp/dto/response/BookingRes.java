package com.hotel.webapp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRes {
  Integer id;
  Integer userId;
  String userName;
  Integer roomId;
  Integer roomNumber;
  LocalDateTime checkInTime;
  LocalDateTime checkOutTime;
  LocalDateTime actualCheckInTime;
  LocalDateTime actualCheckOutTime;
  String notes;
  Boolean status;

  // payment
  Integer paymentId;
  String paymentName;
  BigDecimal amount;
  String notePayment;
  Boolean paymentStatus;

  String createdName;
  String updatedName;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
