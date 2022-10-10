package com.sixsprints.eclinic.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.eclinic.dto.VitalsDto;
import com.sixsprints.eclinic.enums.MasterType;

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
public class HospitalVitals extends AbstractMongoEntity {

  private static final long serialVersionUID = -1048712104669425031L;

  private String tenantId;

  private List<VitalsDto> vitals;
  
  private MasterType type;

}
