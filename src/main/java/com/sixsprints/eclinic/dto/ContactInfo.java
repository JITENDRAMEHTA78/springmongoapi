package com.sixsprints.eclinic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfo {

  private String email;

  private String contactNumber;

  private String addressLine1;

  private String addressLine2;

  private String city;

  private String state;

  private String country;

  private String pincode;

}
