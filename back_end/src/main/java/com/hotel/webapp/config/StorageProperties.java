package com.hotel.webapp.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app.storage")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StorageProperties {
  String root;
  Map<String, String> upload;
  Map<String, String> download;

  public String getUserUploadPath() {
    return root + "/" + upload.get("user");
  }
}
