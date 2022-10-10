package com.sixsprints.eclinic.service;

import static org.hamcrest.CoreMatchers.notNullValue;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.sixsprints.cloudservice.dto.FileDto;
import com.sixsprints.cloudservice.service.CloudStorage;
import com.sixsprints.eclinic.ApplicationTests;

public class CloudStorageTest extends ApplicationTests {

  @Autowired
  private CloudStorage cloudStorage;

  @Value("${google.cloud.storage.bucket}")
  private String bucket;

  @Value("${google.cloud.storage.prescription.subdirectory}")
  private String prescriptionDir;

  @Test
  public void shoudlUpload() {
    String path = cloudStorage.upload(
      FileDto.builder().fileName(prescriptionDir + "testfile.txt").bytes("test content".getBytes()).build(),
      bucket);

    Assert.assertThat(path, notNullValue());
    System.out.println(path);
  }

  @Test
  public void shoudlDownload() throws IOException {
    Path path = cloudStorage.download(prescriptionDir + "testfile.txt", bucket);
    Assert.assertThat(path, notNullValue());
    System.out.println(path);
  }

}
