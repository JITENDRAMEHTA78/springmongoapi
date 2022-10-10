package com.sixsprints.eclinic.dto;

import javax.validation.constraints.NotBlank;

import com.sixsprints.auth.dto.Authenticable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "password")
public class AuthDto implements Authenticable {

  @NotBlank
  private String mobileNumber;
  
  private String email;

  private String password;

  private String name;
  
  private String countryCode;

  @Override
  public String authId() {
    return getMobileNumber();
  }

  @Override
  public String passcode() {
    return getPassword();
  }

}
