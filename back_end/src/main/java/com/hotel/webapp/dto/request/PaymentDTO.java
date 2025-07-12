package com.hotel.webapp.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentDTO {
  Integer bookingId;
  Integer methodId;
  BigDecimal amount;
  String note;
  Boolean status;
}
