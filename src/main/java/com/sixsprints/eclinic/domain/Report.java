package com.sixsprints.eclinic.domain;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sixsprints.core.domain.AbstractMongoEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class Report extends AbstractMongoEntity {

  private static final long serialVersionUID = -4393256642951387306L;

  private String reportName;

  private List<String> images;

  private String patientSlug;

  private Date reportDate;
  
  private String value;
  
  private String action;
}
