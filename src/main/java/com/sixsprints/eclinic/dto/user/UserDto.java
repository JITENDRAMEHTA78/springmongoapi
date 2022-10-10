package com.sixsprints.eclinic.dto.user;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.sixsprints.auth.dto.Authenticable;
import com.sixsprints.eclinic.dto.PermissionDto;

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
public class UserDto implements Authenticable {

  private String id;

  private String slug;

  private String tenantId;

  @NotBlank
  private String mobileNumber;

  private String password;

  @NotBlank
  private String name;

  private String email;

  private String roleName;

  private List<PermissionDto> permissions;

  @Override
  public String authId() {
    return getMobileNumber();
  }

  @Override
  public String passcode() {
    return getPassword();
  }

}
