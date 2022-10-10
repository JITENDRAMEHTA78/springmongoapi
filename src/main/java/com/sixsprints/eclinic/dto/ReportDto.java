package com.sixsprints.eclinic.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
  
  private String id;
  
  private String slug;

  private String reportName;

  private List<String> images;

  private String patientSlug;

  private Date reportDate;
  
  private String value;
  
  private String action;

  private String appointmentSlug;
}
