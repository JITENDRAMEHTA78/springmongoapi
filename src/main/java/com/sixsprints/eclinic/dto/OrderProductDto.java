package com.sixsprints.eclinic.dto;

import com.sixsprints.eclinic.enums.ProductType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductDto {

  @Builder.Default
  private Integer quantity = 1;

  private ProductType productType;

  private String productId;

}
