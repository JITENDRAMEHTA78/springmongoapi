package com.sixsprints.eclinic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatePair {

  private Long from;

  private Long to;

  public boolean isBetween(Long date) {
    return date >= from && date <= to;
  }

}
