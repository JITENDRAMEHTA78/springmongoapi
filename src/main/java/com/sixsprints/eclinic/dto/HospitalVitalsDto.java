package com.sixsprints.eclinic.dto;

import java.util.List;

import com.sixsprints.eclinic.enums.MasterType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalVitalsDto {

  private String id;

  private String tenantId;
  
  private List<VitalsDto> vitals;
  
  private MasterType type;

}
