package com.sixsprints.eclinic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialityDto {

  private String label;

  private String value;

  private int doctorCount;

  private String image;

}
