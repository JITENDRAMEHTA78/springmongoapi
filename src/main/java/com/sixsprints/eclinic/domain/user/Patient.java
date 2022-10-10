package com.sixsprints.eclinic.domain.user;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.sixsprints.core.utils.Subclass;
import com.sixsprints.eclinic.enums.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "user")
@Subclass
public class Patient extends User {

  private static final long serialVersionUID = -8583358247197982704L;

  private Date dob;

  private Gender gender;
  
  private String address;

  @Indexed(unique = true, sparse = true)
  private String uhid;

  @Builder.Default
  private Boolean isOld = Boolean.FALSE;
  
  private Integer age;

}
