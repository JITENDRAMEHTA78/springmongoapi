package com.sixsprints.eclinic.dto;

import java.math.BigDecimal;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotConfigDto {

  private Map<String, String> availabilitySummary;

  private BigDecimal fees;

  private Long appointmentSlotDurationInMillis;

}
