package com.sixsprints.eclinic.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sixsprints.cloudservice.dto.FileDto;
import com.sixsprints.cloudservice.service.CloudStorage;
import com.sixsprints.eclinic.util.Constants;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CloudStorageService {

//  private static final double MAX_IMAGE_SIZE = 1024D;

  @Autowired
  private CloudStorage cloudStorage;

  @Value("${google.cloud.storage.bucket}")
  private String bucket;

  @Value("${server.url}")
  private String endpoint;

  public Path download(String key) throws IOException {
    return cloudStorage.download(key, bucket);
  }

  public String upload(MultipartFile file, String dir) throws IOException {
    String filename = UUID.randomUUID().toString()
      + file.getOriginalFilename().replaceAll(" ", "").replaceAll("/", "");
    log.info("Request to upload {} of size {} KB", filename, file.getSize() / 1024);
    dir = dir.endsWith("/") ? dir : dir + "/";
    cloudStorage.upload(
      FileDto.builder().fileName(dir + filename).bytes(file.getBytes()).build(), bucket);
    return endpoint + Constants.API_PREFIX + "/file" + "?bucket=" + bucket + "&key=" + dir + filename;
  }

  public String upload(FileDto fileDto) {
    log.info("Request to upload {}", fileDto.getFileName());
    cloudStorage.upload(fileDto, bucket);
    return endpoint + Constants.API_PREFIX + "/file" + "?bucket=" + bucket + "&key=" + fileDto.getFileName();
  }

}
