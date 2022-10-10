package com.sixsprints.eclinic;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ApplicationTests {

  @Resource
  private MongoTemplate mongoTemplate;

  @Test
  public void contextLoads() {
  }

  @After
  public void tearDown() {
    mongoTemplate.getDb().drop();
  }
}
