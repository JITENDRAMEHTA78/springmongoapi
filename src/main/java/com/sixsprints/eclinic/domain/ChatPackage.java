package com.sixsprints.eclinic.domain;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.sixsprints.core.domain.AbstractMongoEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Document
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChatPackage extends AbstractMongoEntity {

  private static final long serialVersionUID = -4788207706715073791L;

  @Indexed
  private String doctorSlug;

  private String packageName;

  private Integer totalQuestions;

  private Integer totalDays;

  private BigDecimal amount;
}
