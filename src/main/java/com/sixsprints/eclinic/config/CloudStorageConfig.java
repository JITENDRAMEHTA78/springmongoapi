package com.sixsprints.eclinic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sixsprints.cloudservice.dto.Credentials;
import com.sixsprints.cloudservice.service.CloudStorage;
import com.sixsprints.cloudservice.service.impl.GoogleCloudStorage;

@Configuration
public class CloudStorageConfig {

  @Value(value = "${google.cloud.storage.project.id}")
  private String projectId;

  @Value(value = "${google.cloud.storage.cred.file}")
  private String credFilePath;

  @Bean
  public CloudStorage cloudStorage() {
    Credentials cred = Credentials.builder()
      .file(getClass().getClassLoader().getResourceAsStream(credFilePath)).projectId(projectId).build();
    return new GoogleCloudStorage(cred);
  }

}
