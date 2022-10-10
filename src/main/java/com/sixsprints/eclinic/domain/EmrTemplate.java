package com.sixsprints.eclinic.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.eclinic.dto.PrescriptionDto;

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
@CompoundIndexes({
    @CompoundIndex(unique = true, name = "tenantId_Name", def = "{'name': 1, 'tenantId': 1}") })

public class EmrTemplate extends AbstractMongoEntity {

  private static final long serialVersionUID = 3093091L;

  @Indexed
  private String tenantId;

  @Indexed
  private String name;

  @Indexed
  private String specialitySlug;

  private PrescriptionDto template;

}
