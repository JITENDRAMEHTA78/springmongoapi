package com.sixsprints.eclinic.domain;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.sixsprints.core.domain.AbstractMongoEntity;
import com.sixsprints.eclinic.dto.OrderProductDto;
import com.sixsprints.eclinic.dto.PaymentDto;
import com.sixsprints.eclinic.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Document
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Order extends AbstractMongoEntity {

  private static final long serialVersionUID = 5457222947853227992L;

  private String patientSlug;

  private String doctorSlug;

  private String creatorName;

  private String creatorRole;

  private PaymentDto paymentDetails;

  private OrderProductDto products;

  @Indexed
  private String tenantId;

  @Indexed
  private OrderStatus status;

}
