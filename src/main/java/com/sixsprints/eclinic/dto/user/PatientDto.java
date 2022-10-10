package com.sixsprints.eclinic.dto.user;

import java.util.Date;

import com.sixsprints.eclinic.enums.Gender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PatientDto extends UserDto {

  private Date dob;

  private Gender gender;

  private String address;

  private String uhid;

  private Boolean isOld;
  
  private Date lastVisit;

  private Integer age;
}
