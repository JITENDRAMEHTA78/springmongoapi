package com.sixsprints.eclinic.domain;

import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.eclinic.dto.PermissionDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document
public class Role extends AbstractMongoEntity {

  private static final long serialVersionUID = 6464769316000227488L;

  @Indexed(unique = true)
  private String name;

  @Singular
  private List<PermissionDto> permissions;

}
