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
public class AppointmentDisclaimerDto {

  private AppointmentType appointmentType;

  private String disclaimer;

  private Boolean active;

}
