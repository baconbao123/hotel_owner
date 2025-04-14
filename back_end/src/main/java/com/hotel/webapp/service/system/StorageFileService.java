package com.hotel.webapp.service.system;

import com.hotel.webapp.config.StorageProperties;
import com.hotel.webapp.util.FileHelperUtil;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StorageFileService {
  StorageProperties storageProperties;
  FileHelperUtil fileHelperUtil;

  @PostConstruct
  public void init() {
    createDir(storageProperties.getUserUploadPath());
  }

  private void createDir(String path) {
    File dir = new File(path);
    if (!dir.exists()) {
      boolean created = dir.mkdirs();
      log.info("Created path: {}", path);
      if (!created) log.error("Failed to create path: {}", path);
    }
  }

  public String uploadUserImg(MultipartFile file) throws IOException {
    return saveImage(file, storageProperties.getUserUploadPath());
  }

  private String saveImage(MultipartFile file, String folderPath) throws IOException {
    createDir(folderPath);

    String fileName = fileHelperUtil.generateFileName(file.getOriginalFilename());
    Path path = Paths.get(folderPath, fileName);

    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    return fileName;
  }
}
