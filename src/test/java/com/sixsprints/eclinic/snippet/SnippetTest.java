package com.sixsprints.eclinic.snippet;

import java.time.LocalTime;

import org.joda.time.DateTime;
import org.joda.time.DateTime.Property;
import org.junit.Test;

public class SnippetTest {

  @Test
  public void testLocalTime() {
    LocalTime time = LocalTime.parse("22:03");
    System.out.println(time);
    Property dayOfWeek = DateTime.now().dayOfWeek();
    System.out.println(dayOfWeek.getAsText());
  }

}
