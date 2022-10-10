package com.sixsprints.eclinic.domain;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.eclinic.domain.embed.ColorPalette;

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
@Document
public class Hospital extends AbstractMongoEntity {

  private static final long serialVersionUID = 6226659997838999853L;

  @Indexed
  private String tenantId;

  private String name;

  private String domainName;

  private String logo;

  private String banner;

  private ColorPalette theme;

}
