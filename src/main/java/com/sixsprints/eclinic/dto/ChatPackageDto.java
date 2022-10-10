package com.sixsprints.eclinic.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatPackageDto {

  private String id;
  
  private String doctorSlug;

  @NotNull
  private String packageName;

  @NotNull
  private Integer totalQuestions;

  @NotNull
  private Integer totalDays;

  @NotNull
  private BigDecimal amount;

  private String slug;
}
