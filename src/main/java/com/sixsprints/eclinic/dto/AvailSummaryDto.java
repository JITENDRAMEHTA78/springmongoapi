package com.sixsprints.eclinic.dto;

import java.math.BigDecimal;

import com.sixsprints.eclinic.enums.AppointmentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailSummaryDto {

  private String fromDay;

  private String toDay;

  private String fromSlot;

  private String toSlot;

  private BigDecimal fees;

  private Long appointmentSlotDurationInMillis;

  private AppointmentType appointmentType;

}
