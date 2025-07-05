package com.hotel.webapp.dto.request;

import com.hotel.webapp.validation.FieldNotEmpty;
import com.hotel.webapp.validation.MaxSizeListImg;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelDTO {
  @FieldNotEmpty(field = "Name")
  String name;
  String description;
  @FieldNotEmpty(field = "Status")
  Boolean status;
  Integer ownerId;

  //  approve
  String noteHotel;

  // address ----
  @FieldNotEmpty(field = "Province")
  String provinceCode;
  @FieldNotEmpty(field = "District")
  String districtCode;
  @FieldNotEmpty(field = "Ward")
  String wardCode;
  @FieldNotEmpty(field = "Street")
  Integer streetId;
  @FieldNotEmpty(field = "Street number")
  String streetNumber;
  String note;

  // type
  List<Integer> typeIds;

  // document
  List<DocumentReq> documents;

  // images ---
  @MaxSizeListImg(value = 3)
  List<ImagesReq> images;

  // avatar ---
  AvatarReq avatar;

  // policy ---
  PolicyReq policy;

  // facilities
  List<Integer> facilities;

  @Getter
  @Setter
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class AvatarReq {
    String keepAvatar = "false";
    MultipartFile avatarUrl;
    String existingAvatarUrl;
  }

  @Getter
  @Setter
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class ImagesReq {
    Integer imageId = null;
    String existingImageUrl = null;
    MultipartFile imageFile;
  }

  @Getter
  @Setter
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class DocumentReq {
    Integer documentId = null;
    String documentName;
    Integer typeId;
    @MaxSizeListImg(value = 1)
    MultipartFile documentUrl;
    String existingDocumentUrl;
  }

  @Getter
  @Setter
  @FieldDefaults(level = AccessLevel.PRIVATE)
  public static class PolicyReq {
    Integer policyId;
    String policyName;
    String policyDescription;
  }
}
