package com.sixsprints.eclinic.dto;

import com.sixsprints.auth.dto.ResetPasscode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ResetPasswordDto extends AuthDto implements ResetPasscode {

  private String otp;

  @Override
  public String otp() {
    return otp;
  }

}
