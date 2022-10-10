package com.sixsprints.eclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import com.github.lambdaexpression.annotation.EnableRequestBodyParam;

@SpringBootApplication
@EnableMongoAuditing
@EnableRequestBodyParam
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
