package com.sixsprints.eclinic.domain;

import java.util.List;
import java.util.Locale;

import org.springframework.data.mongodb.core.mapping.Document;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.core.dto.FieldDto;
import com.sixsprints.eclinic.dto.AppointmentDisclaimerDto;
import com.sixsprints.eclinic.dto.VitalsDto;
import com.sixsprints.eclinic.enums.AppointmentStatus;

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
public class Config extends AbstractMongoEntity {

  private static final long serialVersionUID = -944228779044704191L;

  private List<FieldDto> chatPackageField;

  private List<FieldDto> orderField;
  
  private List<FieldDto> appointmentField;

  private List<FieldDto> patientField;

  private List<FieldDto> doctorField;

  private List<FieldDto> emrTemplateFields;
  
  private List<FieldDto> medicationField;

  private List<VitalsDto> vitals;

  private List<VitalsDto> coMorbidities;

  private List<AppointmentStatus> appointmentStatus;
  
  private List<AppointmentDisclaimerDto> appointmentType;

  private Locale selectedLocale;
}
