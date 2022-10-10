package com.sixsprints.eclinic.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDto {

  private List<String> images;

  private List<String> diagrams;

  private String prov;

  private String chiefComplaints;

  private String hopi;

  private String referringDoctor;

  private String referringDoctorMobile;
  
  private String referringDocEmail;
  
  private List<String> otherEmails;

  private String dietaryRestriction;

  private Map<String, String> vitals;

  private Map<String, String> coMorbidities;

  private String remarks;

  private String treatment;

  private List<MedicationDto> medications;

  private List<NoteDto> notes;

  private String surgicalPlan;

  private String qrCode;

  private Date nextAppointmentDate;

  @Builder.Default
  private String followUp = "1 Week";

  private Date date;

  private String examination;

  private String investigation;

  private Boolean printLetterHead;

  private Boolean isDetailedConsult;
  
  private Boolean isFollowUpRequired;
  
  private String appoinmentType;

  // In vh. To be used in the template as <headerSpacing + 'vh'>
  private Integer headerSpacing;
  
  private Boolean testNeeded;

  private List<String> testName;
  
  private String action;

}
