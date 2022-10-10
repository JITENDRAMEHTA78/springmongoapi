package com.sixsprints.eclinic.dto;

import com.sixsprints.eclinic.enums.MedicationInterval;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MedicationDto {

  private String id;

  private String slug;

  private String name;

  private String dose;

  private String frequency;

  private int num;

  private MedicationInterval interval;

  private String remarks;

}
