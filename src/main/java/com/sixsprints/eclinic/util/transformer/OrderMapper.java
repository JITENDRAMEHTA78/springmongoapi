package com.sixsprints.eclinic.util.transformer;

import javax.annotation.Resource;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.sixsprints.core.transformer.GenericTransformer;
import com.sixsprints.eclinic.domain.Appointment;
import com.sixsprints.eclinic.domain.Order;
import com.sixsprints.eclinic.dto.OrderDto;
import com.sixsprints.eclinic.dto.PaymentDto;
import com.sixsprints.eclinic.enums.ProductType;
import com.sixsprints.eclinic.service.AppointmentService;
import com.sixsprints.eclinic.service.user.DoctorService;
import com.sixsprints.eclinic.service.user.PatientService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Mapper(componentModel = "spring")
public abstract class OrderMapper extends GenericTransformer<Order, OrderDto> {

  @Autowired
  private PatientService patientService;

  @Autowired
  private PatientMapper patientMapper;

  @Resource
  private DoctorService doctorService;

  @Resource
  private DoctorMapper doctorMapper;

  @Resource
  private AppointmentService appointmentService;

  @Override
  public abstract OrderDto toDto(Order chatPackageBilling);

  @Override
  public abstract Order toDomain(OrderDto dto);

  @AfterMapping
  protected void afterToDto(Order domain, @MappingTarget OrderDto.OrderDtoBuilder builder) {

    try {
      builder.doctor(doctorMapper.toDto(doctorService.findBySlug(domain.getDoctorSlug())));
      builder.patient(patientMapper.toDto(patientService.findBySlug(domain.getPatientSlug())));
      if (domain.getProducts() != null && ProductType.APPOINTMENT.equals(domain.getProducts().getProductType())) {
        Appointment appointment = appointmentService.findBySlug(domain.getProducts().getProductId());
        builder.appointmentDate(appointment.getDateTime());
      } else {
        builder.appointmentDate(domain.getDateCreated());
      }

      PaymentDto paymentDetails = domain.getPaymentDetails();
      if (paymentDetails != null) {
        paymentDetails.setIsReceiptPrinted(
          paymentDetails.getIsReceiptPrinted() == null ? false : paymentDetails.getIsReceiptPrinted());
        builder.paymentDetails(paymentDetails);
      }

    } catch (Exception e) {
      log.info("Error in orderMapper " + e);
    }

  }
}
