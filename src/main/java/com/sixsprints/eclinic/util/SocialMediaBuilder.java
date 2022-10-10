package com.sixsprints.eclinic.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sixsprints.eclinic.dto.SocialMediaDto;

@Component
public class SocialMediaBuilder {

  public static List<SocialMediaDto> socialMedia() {

    SocialMediaDto s1 = SocialMediaDto.builder().name("FACEBOOK")
      .link("https://www.facebook.com/ramantanwarurology/").build();

    SocialMediaDto s2 = SocialMediaDto.builder().name("INSTAGRAM")
      .link("https://www.instagram.com/drramantanwar").build();

    SocialMediaDto s3 = SocialMediaDto.builder().name("LINKEDIN")
      .link("https://www.linkedin.com/in/ramantanwar").build();
    
    SocialMediaDto s4 = SocialMediaDto.builder().name("WEBSITE")
      .link("https://ramantanwar.in").build();

    return Arrays.asList(s1, s2, s3, s4);
  }
  
  public static List<SocialMediaDto> socialMedia2() {

    SocialMediaDto s1 = SocialMediaDto.builder().name("FACEBOOK")
      .link("https://www.facebook.com/jyotihospitalgurgaon/").build();

    SocialMediaDto s2 = SocialMediaDto.builder().name("INSTAGRAM")
      .link("https://www.instagram.com/dr.milindtanwar").build();

    SocialMediaDto s3 = SocialMediaDto.builder().name("LINKEDIN")
      .link("http://linkedin.com/in/milind-tanwar-4940b895").build();
    
    SocialMediaDto s4 = SocialMediaDto.builder().name("WEBSITE")
      .link("https://milindtanwar.in").build();

    return Arrays.asList(s1, s2, s3, s4);
  }
}
