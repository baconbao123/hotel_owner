package com.hotel.webapp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelsRes {
  Integer id;
  String name;
  String ownerName;
  String description;
  String avatarUrl;
  Boolean status;

  @Getter
  @Setter
  @AllArgsConstructor
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class HotelRes {
    // info ---
    Integer id;
    String name;
    String description;
    Boolean status;
    String createdName;
    String updatedName;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    // avatar ---
    String avatarUrl;

    // address ---
    String streetNumber;
    Integer streetId;
    String wardCode;
    String wardName;
    String districtCode;
    String districtName;
    String provinceCode;
    String provinceName;
    String streetName;
    String note;

    // images ---
    List<ImagesRes> images;

    // type hotel ---
    List<TypeHotelRes> typeHotels;

    // facilities ---
    List<FacilitiesRes> facilities;

    // documents ---
    List<DocumentHotelRes> documents;

    // policies ---
    PolicyRes policies;

    // hotel
    String hotelNote;
    String ownerName;
    Integer ownerId;

    @Getter
    @Setter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ImagesRes {
      Integer id;
      String imagesUrl;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TypeHotelRes {
      Integer id;
      String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class FacilitiesRes {
      Integer id;
      String name;
      String icon;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DocumentHotelRes {
      Integer documentId;
      String documentName;
      Integer typeId;
      String typeName;
      String documentUrl;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PolicyRes {
      Integer id;
      String policyName;
      String policyDescription;
    }
  }
}
