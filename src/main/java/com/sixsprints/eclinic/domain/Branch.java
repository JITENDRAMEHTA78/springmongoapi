package com.sixsprints.eclinic.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sixsprints.core.domain.AbstractMongoEntity;

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
public class Branch extends AbstractMongoEntity {

  private static final long serialVersionUID = -3688082885157781779L;

  private String address;

  private String contactNumber;

  private String contactEmailId;

}
