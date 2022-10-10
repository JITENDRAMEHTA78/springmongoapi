package com.sixsprints.eclinic.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.index.Indexed;

import com.sixsprints.eclinic.dto.user.DoctorDto;
import com.sixsprints.eclinic.dto.user.PatientDto;
import com.sixsprints.eclinic.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

  private String id;

  private String slug;

  private Date dateCreated;

  @NotNull
  private String doctorSlug;

  private String patientSlug;

  private DoctorDto doctor;

  private PatientDto patient;

  private PaymentDto paymentDetails;

  private OrderProductDto products;
  
  private Date appointmentDate;

  @Indexed
  private OrderStatus status;

}
