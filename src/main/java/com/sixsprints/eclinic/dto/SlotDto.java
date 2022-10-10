package com.sixsprints.eclinic.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotDto implements Comparable<SlotDto> {

  private LocalTime time;

  private Boolean isAlreadyBooked;

  @Override
  public int compareTo(SlotDto o) {
    return time.compareTo(o.time);
  }

}
