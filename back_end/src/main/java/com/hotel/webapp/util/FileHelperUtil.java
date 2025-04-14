package com.hotel.webapp.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FileHelperUtil {

  public String generateFileName(String fileName) {
    var newFileName = UUID.randomUUID().toString().replace("-", "");
    String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
    return newFileName + "." + ext;
  }
}
