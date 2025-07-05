package com.hotel.webapp.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImagesReq {
  Integer imageId = null;
  String existingImageUrl = null;
  MultipartFile imageFile;
}
