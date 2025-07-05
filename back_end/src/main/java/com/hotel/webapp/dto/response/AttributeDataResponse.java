package com.hotel.webapp.dto.response;

import com.hotel.webapp.entity.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttributeDataResponse {
  List<LocalResponse> provinces;
  List<PaymentMethod> paymentMethods;
  List<DocumentType> documentTypes;
  List<RoomType> roomTypes;
  List<UserRes.CustomerRes> customers;
  List<TypeHotel> hotelTypes;
  List<Facilities> hotelFacilities;
  List<PricesDTO> pricesRoom;

}
