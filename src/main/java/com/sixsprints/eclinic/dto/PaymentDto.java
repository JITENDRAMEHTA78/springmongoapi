package com.sixsprints.eclinic.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.sixsprints.eclinic.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {

  @NotNull
  private PaymentMode mode;

  private String paymentMode;

  private String utr;

  @NotNull
  private BigDecimal amount;

  private Boolean isReceiptPrinted;

}
