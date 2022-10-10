package com.sixsprints.eclinic.dto;

import java.math.BigDecimal;
import java.time.DayOfWeek;
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
public class SlotResponseDto {

  private String doctorSlug;

  private Date date;

  private DayOfWeek dayOfWeek;

  private List<SlotDto> earlyMorning;

  private List<SlotDto> midDay;

  private List<SlotDto> evening;

  private BigDecimal fees;

}
