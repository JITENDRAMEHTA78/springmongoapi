package com.sixsprints.eclinic.dto;

import com.sixsprints.eclinic.enums.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOptionDto {

  private PaymentMode paymentMode;

  private String payTo;

  private String description;

  private String qrCode;

  private String logo;
}
