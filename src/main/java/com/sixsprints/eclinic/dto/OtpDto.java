package com.sixsprints.eclinic.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpDto {

  private String id;

  private String authId;

  private String otp;

  private Date dateModified;

}
