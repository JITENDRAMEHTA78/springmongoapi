package com.sixsprints.eclinic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorConfig {

  private Long appointmentSlotDurationInMillis;

}
