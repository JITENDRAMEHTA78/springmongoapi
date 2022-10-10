package com.sixsprints.eclinic.dto;

import com.sixsprints.eclinic.enums.AppointmentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentTypeDto {

  private AppointmentType label;

  private String disclaimer;

  private Boolean active;
}
