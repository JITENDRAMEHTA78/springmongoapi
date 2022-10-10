package com.sixsprints.eclinic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.sixsprints.core.config.ParentMongoConfig;
import com.sixsprints.core.repository.InheritanceAwareMongoRepositoryFactoryBean;

@Configuration
@EnableMongoRepositories(repositoryFactoryBeanClass = InheritanceAwareMongoRepositoryFactoryBean.class, basePackages = {
    "com.sixsprints.core", "com.sixsprints.eclinic", "com.sixsprints.auth" })
public class MongoConfig extends ParentMongoConfig {

  @Value(value = "${spring.data.mongodb.host}")
  private String host;

  @Value(value = "${spring.data.mongodb.database}")
  private String database;

  @Value(value = "${spring.data.mongodb.port}")
  private Integer port;

  @Override
  protected String getHost() {
    return host;
  }

  @Override
  protected Integer getPort() {
    return port;
  }

  @Override
  protected String getDatabase() {
    return database;
  }

}
