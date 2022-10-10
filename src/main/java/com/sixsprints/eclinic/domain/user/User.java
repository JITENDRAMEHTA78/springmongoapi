package com.sixsprints.eclinic.domain.user;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.sixsprints.auth.domain.AbstractAuthenticableEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@CompoundIndex(unique = true, name = "tenantId_mobileNumber", def = "{'tenantId' : 1, 'mobileNumber': 1}")
@Document(collection = "user")
public class User extends AbstractAuthenticableEntity {

  private static final long serialVersionUID = -744326052717775415L;

  @Indexed
  private String tenantId;

  @Indexed
  private String mobileNumber;

  private String name;

  private String email;

  private String roleName;

  @Override
  public String authId() {
    return getMobileNumber();
  }

}
