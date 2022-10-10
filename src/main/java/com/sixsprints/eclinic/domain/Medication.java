package com.sixsprints.eclinic.domain;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.eclinic.enums.MedicationInterval;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Document
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@CompoundIndexes({
    @CompoundIndex(name = "name_dose", def = "{'name': 1, 'dose' : 1}", unique = true) })

public class Medication extends AbstractMongoEntity {

  private static final long serialVersionUID = -5008265853675964107L;

  private String name;

  private String dose;

  private String frequency;

  private int num;

  private MedicationInterval interval;

  private String remarks;

}
