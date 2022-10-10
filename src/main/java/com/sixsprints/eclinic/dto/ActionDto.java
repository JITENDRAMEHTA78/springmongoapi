package com.sixsprints.eclinic.dto;

import com.sixsprints.eclinic.enums.ActionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionDto {

  private String name;

  private String actionUrl;
  
  private ActionType type;

}
